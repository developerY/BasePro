package com.ylabz.basepro.core.data.repository.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A general-purpose timer.  Emits elapsed milliseconds once per second when running.
 * Pause freezes; stop resets back to zero.
 */
@Singleton
class TimerRepositoryImpl @Inject constructor() : TimerRepository {
    // Backing state flows
    private val _timerState = MutableStateFlow(TimerState.Idle)
    private val _elapsedTime = MutableStateFlow(0L)

    override val timerState: Flow<TimerState> = _timerState.asStateFlow()
    override val elapsedTime: Flow<Long> = _elapsedTime.asStateFlow()

    // Internal coroutine scope for ticking
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // When running, this job updates elapsedTime every second
    private var tickerJob: Job? = null

    // Bookkeeping for elapsed calculation
    private var startTimeMs = 0L
    private var accumulatedMs = 0L

    override fun start() {
        // If already running, do nothing
        if (_timerState.value == TimerState.Running) return

        // Record start point
        startTimeMs = System.currentTimeMillis()
        _timerState.value = TimerState.Running

        // Launch a ticker that updates once per second
        tickerJob = scope.launch {
            while (isActive && _timerState.value == TimerState.Running) {
                val now = System.currentTimeMillis()
                val elapsed = accumulatedMs + (now - startTimeMs)
                _elapsedTime.value = elapsed
                delay(1_000L)
            }
        }
    }

    override fun pause() {
        // Only pause if we’re actually running
        if (_timerState.value != TimerState.Running) return

        // Accumulate the time so far, cancel ticker
        accumulatedMs += System.currentTimeMillis() - startTimeMs
        tickerJob?.cancel()
        tickerJob = null
        _timerState.value = TimerState.Paused
    }

    override fun stop() {
        // Cancel any running ticker
        tickerJob?.cancel()
        tickerJob = null

        // Reset everything
        _timerState.value = TimerState.Idle
        accumulatedMs = 0L
        startTimeMs = 0L
        _elapsedTime.value = 0L
    }
}
