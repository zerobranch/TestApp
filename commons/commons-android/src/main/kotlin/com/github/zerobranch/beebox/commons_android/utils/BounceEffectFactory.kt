package com.github.zerobranch.beebox.commons_android.utils

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f
private const val FLING_TRANSLATION_MAGNITUDE = 0.5f

class BounceEffectFactory(var isVertical: Boolean = true) : RecyclerView.EdgeEffectFactory() {

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {

        return object : EdgeEffect(recyclerView.context) {
            var translationAnim: SpringAnimation? = null

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                if (isVertical) {
                    val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                    val translationYDelta =
                        sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    recyclerView.translationY += translationYDelta
                } else {
                    val sign = if (direction == DIRECTION_RIGHT) -1 else 1
                    val translationXDelta =
                        sign * recyclerView.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    recyclerView.translationX += translationXDelta
                }
                translationAnim?.cancel()
            }

            override fun onRelease() {
                super.onRelease()
                if (isVertical) {
                    if (recyclerView.translationY != 0f) {
                        translationAnim = createAnim()?.also { it.start() }
                    }
                } else {
                    if (recyclerView.translationX != 0f) {
                        translationAnim = createAnim()?.also { it.start() }
                    }
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                val sign = if (isVertical) {
                    if (direction == DIRECTION_BOTTOM) -1 else 1
                } else {
                    if (direction == DIRECTION_RIGHT) -1 else 1
                }
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                translationAnim?.cancel()
                translationAnim = createAnim().setStartVelocity(translationVelocity)?.also { it.start() }
            }

            override fun draw(canvas: Canvas?): Boolean {
                return false
            }

            override fun isFinished(): Boolean {
                return translationAnim?.isRunning?.not() ?: true
            }

            private fun createAnim() =
                SpringAnimation(
                    recyclerView,
                    if (isVertical) SpringAnimation.TRANSLATION_Y
                    else SpringAnimation.TRANSLATION_X
                ).setSpring(
                    SpringForce()
                        .setFinalPosition(0f)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                        .setStiffness(SpringForce.STIFFNESS_LOW)
                )
        }
    }
}