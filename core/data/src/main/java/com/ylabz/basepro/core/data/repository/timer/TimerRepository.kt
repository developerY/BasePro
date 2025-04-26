package com.ylabz.basepro.core.data.repository.timer

import kotlinx.coroutines.flow.Flow

/** A simple timer API: start/resume, pause, stop (reset). */
interface TimerRepository {
    /** Milliseconds elapsed since last (re)start, updates once per second while running. */
    val elapsedTime: Flow<Long>

    /** Current state of the timer. */
    val timerState: Flow<TimerState>

    /** Start or resume the timer. */
    fun start()

    /** Pause the timer (freezes elapsedTime). */
    fun pause()

    /** Stop and reset the timer (elapsedTime → 0, state → Idle). */
    fun stop()
}

enum class TimerState {
    Idle,
    Running,
    Paused
}
