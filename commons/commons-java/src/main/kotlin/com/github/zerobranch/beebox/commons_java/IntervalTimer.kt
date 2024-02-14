package com.github.zerobranch.beebox.commons_java

import java.util.Timer
import java.util.TimerTask

class IntervalTimer(private val tick: (Long) -> Unit) {
    var isPaused = false
        private set

    val isCanceled: Boolean
        get() = timer == null

    private var timer: Timer? = null
    private var currentTime = 0L

    fun relaunch() {
        stop()
        schedule()
    }

    fun stop() {
        timer?.cancel()
        timer = null
        currentTime = 0
        isPaused = false
    }

    fun pause() {
        if (timer == null) {
            return
        }

        isPaused = true
        timer?.cancel()
        timer = null
    }

    fun resume() {
        if (isPaused) {
            isPaused = false
            schedule()
        }
    }

    private fun schedule() {
        if (timer != null) {
            stop()
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                currentTime++
                tick.invoke(currentTime)
            }
        }, 1000, 1000)
    }
}