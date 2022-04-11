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
        private const val ONE_SECOND_IN_MILLIS = 1_000L
    }

    private var countDownTimer: CountDownTimer? = null

    private val _mainUiState = MutableStateFlow(
        MainUiState(
            leftTimeInMillis = 0L,
            isTimerRunning = false,
            isTimerFinished = false
        )
    )
    val mainUiState: StateFlow<MainUiState> = _mainUiState

    fun setTimer(timeInMills: Long) {
        countDownTimer?.cancel()
        _mainUiState.update { it.copy(leftTimeInMillis = timeInMills, isTimerRunning = false) }
    }

    fun startTimer() {
        viewModelScope.launch {
            countDownTimer =
                object :
                    CountDownTimer(_mainUiState.value.leftTimeInMillis, ONE_SECOND_IN_MILLIS) {
                    override fun onTick(millisUntilFinished: Long) {
                        _mainUiState.update {
                            it.copy(
                                leftTimeInMillis = millisUntilFinished,
                                isTimerRunning = true
                            )
                        }
                    }

                    override fun onFinish() {
                        _mainUiState.update {
                            it.copy(
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
        _mainUiState.update { it.copy(isTimerRunning = false) }
    }

    fun resetTimer() {
        _mainUiState.update {
            it.copy(leftTimeInMillis = 0L, isTimerRunning = false, isTimerFinished = false)
        }
    }
}