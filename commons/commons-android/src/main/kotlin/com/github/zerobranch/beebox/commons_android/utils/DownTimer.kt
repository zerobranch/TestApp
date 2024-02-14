package com.github.zerobranch.beebox.commons_android.utils

import android.os.CountDownTimer

open class DownTimer(private val millisInFuture: Long, private val interval: Long) {
    private var countDownTimer: CountDownTimer? = null
    private var remainingTime: Long = millisInFuture
    private var isTimerPaused: Boolean = true

    open fun onTick(millisUntilFinished: Long) {}

    open fun onFinish() {}

    @Synchronized
    fun start() {
        cancel()
        launchTimer(millisInFuture, interval)
    }

    @Synchronized
    fun resume() {
        if (isTimerPaused) {
            launchTimer(remainingTime, interval)
        }
    }

    fun cancel() {
        countDownTimer?.cancel()
        countDownTimer = null
        remainingTime = millisInFuture
        isTimerPaused = true
    }

    fun pause() {
        countDownTimer?.cancel()
        isTimerPaused = true
    }

    private fun launchTimer(millisInFuture: Long, interval: Long) {
        countDownTimer = object : CountDownTimer(millisInFuture, interval) {
            override fun onFinish() {
                this@DownTimer.cancel()
                this@DownTimer.onFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                this@DownTimer.onTick(millisUntilFinished)
            }
        }

        countDownTimer?.start()
        isTimerPaused = false
    }
}
