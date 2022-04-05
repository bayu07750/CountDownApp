package com.bayu.countdownapp

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val leftTimeInMillis: Long,
    val isTimerRunning: Boolean,
    val isTimerFinished: Boolean,
)

class MainViewModel : ViewModel() {

    companion object {
        private const val START_TIME_IN_MILLIS = 60_000L
        private const val ONE_SECOND_IN_MILLIS = 1_000L
    }

    private var countDownTimer: CountDownTimer? = null

    private val _leftTimeInMillis = MutableStateFlow(
        MainUiState(
            START_TIME_IN_MILLIS,
            isTimerRunning = false,
            isTimerFinished = false
        )
    )
    val leftTimeInMillis: StateFlow<MainUiState> = _leftTimeInMillis

    fun startTimer() {
        viewModelScope.launch {
            countDownTimer =
                object :
                    CountDownTimer(_leftTimeInMillis.value.leftTimeInMillis, ONE_SECOND_IN_MILLIS) {
                    override fun onTick(millisUntilFinished: Long) {
                        _leftTimeInMillis.update {
                            _leftTimeInMillis.value.copy(
                                leftTimeInMillis = millisUntilFinished,
                                isTimerRunning = true
                            )
                        }
                    }

                    override fun onFinish() {
                        _leftTimeInMillis.update {
                            _leftTimeInMillis.value.copy(
                                isTimerRunning = false,
                                isTimerFinished = true
                            )
                        }
                    }
                }
            countDownTimer?.start()
        }
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        _leftTimeInMillis.update { _leftTimeInMillis.value.copy(isTimerRunning = false) }
    }

    fun resetTimer() {
        _leftTimeInMillis.update {
            _leftTimeInMillis.value.copy(
                leftTimeInMillis = START_TIME_IN_MILLIS,
                isTimerRunning = false,
                isTimerFinished = false,
            )
        }
    }
}