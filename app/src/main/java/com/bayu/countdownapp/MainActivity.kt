package com.bayu.countdownapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bayu.countdownapp.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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
        binding.tvCountDown.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Selec timer")
                .build()

            picker.addOnPositiveButtonClickListener {
                val minute = picker.minute
                val minuteInMilli = minute.toLong() * 60 * 1000
                viewModel.setTimer(minuteInMilli)
            }

            picker.show(supportFragmentManager, "picker timer")
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
                            binding.btnStartPause.setOnClickListener {
                                if (leftTimeInMillis <= 0L) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Please set the timer",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    return@setOnClickListener
                                }
                                viewModel.startTimer()
                            }
                        }
                        isTimerRunning && !isTimerFinished -> {
                            updateCountText(leftTimeInMillis)
                            binding.btnStartPause.text = getString(R.string.pause)
                            binding.btnStartPause.setOnClickListener {
                                viewModel.pauseTimer()
                            }
                        }
                        !isTimerRunning && isTimerFinished -> {
                            binding.btnStartPause.text = getString(R.string.reset)
                            binding.btnStartPause.setOnClickListener {
                                viewModel.resetTimer()
                            }
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