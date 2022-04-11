package com.bayu.countdownapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bayu.countdownapp.ui.theme.CountDownAppTheme
import java.util.*

class ComopseActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountDownAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val scaffoldState = rememberScaffoldState()
    val mainUiState by viewModel.mainUiState.collectAsState()

    Scaffold(
        modifier = Modifier,
        scaffoldState = scaffoldState,
        topBar = {
            HomeTopAppBar(title = "Compose Count Down App")
        },
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            leftTimeInMillis = mainUiState.leftTimeInMillis,
            isTimerRunning = mainUiState.isTimerRunning,
            isTimerFinished = mainUiState.isTimerFinished,
            startTimer = viewModel::startTimer,
            pauseTimer = viewModel::pauseTimer,
            resetTimer = viewModel::resetTimer,
            setTimer = viewModel::setTimer
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    leftTimeInMillis: Long,
    isTimerRunning: Boolean,
    isTimerFinished: Boolean,
    startTimer: () -> Unit,
    pauseTimer: () -> Unit,
    resetTimer: () -> Unit,
    setTimer: (Long) -> Unit,
) {
    val leftTimeInMinute = (leftTimeInMillis / 1000) / 60
    val leftTimeInSecond = (leftTimeInMillis / 1000) % 60
    val timeLeftFormatted =
        String.format(Locale.getDefault(), "%02d:%02d", leftTimeInMinute, leftTimeInSecond)

    val textButton = when {
        !isTimerRunning && !isTimerFinished -> stringResource(id = R.string.start)
        isTimerRunning && !isTimerFinished -> stringResource(id = R.string.pause)
        !isTimerRunning && isTimerFinished -> stringResource(id = R.string.reset)
        else -> ""
    }

    val onBtnClicked = when (textButton) {
        stringResource(id = R.string.start) -> startTimer
        stringResource(id = R.string.pause) -> pauseTimer
        stringResource(id = R.string.reset) -> resetTimer
        else -> {
            {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = timeLeftFormatted,
                style = MaterialTheme.typography.h2,
                modifier = Modifier
                    .clickable {
                        setTimer.invoke(10_000L)
                    }
            )
            Button(
                onClick = onBtnClicked
            ) {
                Text(text = textButton)
            }
        }
    }
}

@Composable
fun HomeTopAppBar(
    title: String,
) {
    TopAppBar(
        title = {
            Text(text = title)
        }
    )
}
