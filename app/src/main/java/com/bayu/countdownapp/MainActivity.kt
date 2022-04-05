package com.bayu.countdownapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bayu.countdownapp.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observe()
        actions()
    }

    private fun actions() {
        binding.btnStartPause.setOnClickListener {
            when (binding.btnStartPause.text.toString().trim()) {
                getString(R.string.start) -> {
                    viewModel.startTimer()
                }
                getString(R.string.pause) -> {
                    viewModel.pauseTimer()
                }
                getString(R.string.reset) -> {
                    viewModel.resetTimer()
                }
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.leftTimeInMillis
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { state ->
                    val (leftTimeInMillis, isTimerRunning, isTimerFinished) = state
                    when {
                        !isTimerRunning && !isTimerFinished -> {
                            updateCountText(leftTimeInMillis)
                            binding.btnStartPause.text = getString(R.string.start)
                        }
                        isTimerRunning && !isTimerFinished -> {
                            updateCountText(leftTimeInMillis)
                            binding.btnStartPause.text = getString(R.string.pause)
                        }
                        !isTimerRunning && isTimerFinished -> {
                            binding.btnStartPause.text = getString(R.string.reset)
                        }
                    }
                }
        }
    }

    private fun updateCountText(leftTimeInMillis: Long) {
        val minute = (leftTimeInMillis / 1000) / 60
        val second = (leftTimeInMillis / 1000) % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minute, second)
        binding.tvCountDown.text = timeLeftFormatted
    }
}