package com.example.pam_lab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var timerJob: Job? = null
    private var routeName: String? = null

    fun toggleTimer(routeName: String?) {
        if (_running.value) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        _running.value = true
        timerJob = viewModelScope.launch {
            while (_running.value) {
                delay(1000)
                _timerState.value++
            }
        }
    }

    fun restartTimer(routeName: String) {
        this.routeName = routeName
        _timerState.value = 0
        this.startTimer()
    }

    fun stopTimer() {
        _running.value = false
        timerJob?.cancel()
    }

    fun checkCorrectRoute(routeName: String): Boolean {
        return this.routeName == routeName
    }

    fun displayTime(): String {
        val h = timerState.value / 3600
        val m = (timerState.value % 3600) / 60
        val s = timerState.value % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }
}