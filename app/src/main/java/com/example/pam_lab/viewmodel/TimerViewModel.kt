package com.example.pam_lab.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_lab.database.AppDatabase
import com.example.pam_lab.database.RouteTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimerViewModel(context: Context): ViewModel() {
    private val db = AppDatabase.getInstance(context)
    private val timerDao = db.routeTimerDao()
    private val sharedPref: SharedPreferences = context.getSharedPreferences("timer_prefs", Context.MODE_PRIVATE)

    private val _timerState = MutableStateFlow(0)
    val timerState: StateFlow<Int> = _timerState.asStateFlow()

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()

    private val _currentRouteName = MutableStateFlow<String?>(null)
    val currentRouteName: StateFlow<String?> = _currentRouteName.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    init {
        restoreTimer()
    }

    private fun restoreTimer() {
        val isRunning = sharedPref.getBoolean("isRunning", false)
        val startTime = sharedPref.getLong("startTime", 0L)
        val accumulatedTime = sharedPref.getInt("accumulatedTime", 0)
        val routeName = sharedPref.getString("routeName", null)

        if (routeName != null) {
            _currentRouteName.value = routeName
            if (isRunning && startTime > 0L) {
                val elapsedSinceStart = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                _timerState.value = accumulatedTime + elapsedSinceStart
                startTimer(routeName)
            } else {
                _timerState.value = accumulatedTime
                _running.value = false
            }
        }
    }

    private fun saveTimerState() {
        sharedPref.edit().apply {
            putBoolean("isRunning", _running.value)
            putInt("accumulatedTime", _timerState.value)
            putString("routeName", _currentRouteName.value)
            if (_running.value) {
                putLong("startTime", System.currentTimeMillis())
            } else {
                putLong("startTime", 0L)
            }
            apply()
        }
    }

    // Observe all saved times for specific routes
    val allSavedTimes: StateFlow<List<RouteTimer>> = timerDao.getAllFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun startTimer(routeName: String?) {
        if (timerJob?.isActive == true) return
        
        if (_timerState.value == 0) {
            _currentRouteName.value = routeName
        }

        _running.value = true
        saveTimerState()
        
        timerJob = viewModelScope.launch {
            while (_running.value) {
                delay(1000)
                _timerState.value++
                // Opcjonalnie: zapisuj stan co jakiś czas (np. co 10s), 
                // ale najważniejsze jest zapisanie przy pauzie/starcie.
            }
        }
    }

    fun stopTimer() {
        _running.value = false
        timerJob?.cancel()
        timerJob = null
        saveTimerState()
    }

    fun toggleTimer(routeName: String?) {
        if (_running.value) {
            stopTimer()
        } else {
            startTimer(routeName)
        }
    }

    fun restartTimer() {
        stopTimer()
        _timerState.value = 0
        _currentRouteName.value = null
        sharedPref.edit().clear().apply()
    }
    
    fun restartAndStart(routeName: String?) {
        stopTimer()
        _timerState.value = 0
        _currentRouteName.value = routeName
        startTimer(routeName)
    }

    fun isTimerForRoute(routeName: String?): Boolean {
        return _currentRouteName.value == routeName
    }

    fun isTimerActive(): Boolean {
        return _timerState.value > 0 || _running.value
    }

    fun formatTime(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }

    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun displayTime(): String = formatTime(_timerState.value)

    fun saveTimeToDb(context: Context) {
        val routeName = _currentRouteName.value
        val time = _timerState.value
        
        if (routeName != null && time > 0) {
            viewModelScope.launch {
                timerDao.insertTimer(
                    RouteTimer(
                        routeName = routeName, 
                        timeInSeconds = time,
                        date = System.currentTimeMillis()
                    )
                )
                Toast.makeText(context, "Czas zapisany!", Toast.LENGTH_SHORT).show()
                restartTimer()
            }
        }
    }

    fun deleteTime(timeRecord: RouteTimer) {
        viewModelScope.launch {
            timerDao.deleteTimer(timeRecord)
        }
    }
}
