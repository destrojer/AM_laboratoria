package com.example.pam_lab.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_lab.database.AppDatabase
import com.example.pam_lab.database.RouteTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class TimerViewModel: ViewModel() {
    private val _timerState = MutableStateFlow(0)
    val timerState: StateFlow<Int> = _timerState.asStateFlow()

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()

    private val _currentRouteName = MutableStateFlow<String?>(null)
    val currentRouteName: StateFlow<String?> = _currentRouteName.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer(routeName: String?) {
        if (timerJob?.isActive == true) return
        
        // Lock the timer to this route if it's just starting
        if (_timerState.value == 0) {
            _currentRouteName.value = routeName
        }

        _running.value = true
        timerJob = viewModelScope.launch {
            while (_running.value) {
                delay(1000)
                _timerState.value++
            }
        }
    }

    fun stopTimer() {
        _running.value = false
        timerJob?.cancel()
        timerJob = null
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
    }
    
    fun restartAndStart(routeName: String?) {
        stopTimer()
        _timerState.value = 0
        // We keep the current route name or reset it to the new one
        _currentRouteName.value = routeName
        startTimer(routeName)
    }

    fun isTimerForRoute(routeName: String?): Boolean {
        return _currentRouteName.value == routeName
    }

    fun isTimerActive(): Boolean {
        return _timerState.value > 0 || _running.value
    }

    fun displayTime(): String {
        val h = _timerState.value / 3600
        val m = (_timerState.value % 3600) / 60
        val s = _timerState.value % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }

    fun saveTimeToDb(context: Context) {
        val routeName = _currentRouteName.value
        val time = _timerState.value
        
        if (routeName != null && time > 0) {
            viewModelScope.launch {
                val db = AppDatabase.getInstance(context)
                db.routeTimerDao().insertTimer(RouteTimer(routeName = routeName, timeInSeconds = time))
                Toast.makeText(context, "Czas zapisany dla: $routeName", Toast.LENGTH_SHORT).show()
                restartTimer()
            }
        } else {
            Toast.makeText(context, "Brak czasu do zapisania!", Toast.LENGTH_SHORT).show()
        }
    }
}
