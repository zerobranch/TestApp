package com.github.zerobranch.beebox.commons_android.utils.ext

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader

fun Paint.applyGradientShader(startPosition: GradientStartPosition, colorArray: IntArray, cx: Float, cy: Float): Paint = apply {
    if (colorArray.isNotEmpty()) {
        val positions = getPositions(startPosition, cx, cy)
        shader = LinearGradient(
            positions[0], positions[1],
            positions[2], positions[3],
            colorArray, colorArray.arrayGradientPositions,
            Shader.TileMode.CLAMP
        )
    }
}

private fun getPositions(gradientStartPosition: GradientStartPosition, cx: Float, cy: Float): FloatArray {
    var x0 = 1f
    var y0 = 1f
    var x1 = 1f
    var y1 = 1f
    when (gradientStartPosition) {
        GradientStartPosition.BOTTOM_LEFT -> {
            x0 = 0f
            y0 = cy
            x1 = cx
            y1 = 0f
        }
        GradientStartPosition.BOTTOM_RIGHT -> {
            x0 = cx
            y0 = cy
            x1 = 0f
            y1 = 0f
        }
        GradientStartPosition.TOP_LEFT -> {
            x0 = 0f
            y0 = 0f
            x1 = cx
            y1 = cy
        }
        GradientStartPosition.TOP_RIGHT -> {
            x0 = cx
            y0 = 0f
            x1 = 0f
            y1 = cy
        }
        GradientStartPosition.MIDDLE_LEFT -> {
            x0 = 0f
            y0 = cy / 2f
            x1 = cx
            y1 = cy / 2f
        }
        GradientStartPosition.MIDDLE_RIGHT -> {
            x0 = cx
            y0 = cy / 2f
            x1 = 0f
            y1 = cy / 2f
        }
        GradientStartPosition.MIDDLE_TOP -> {
            x0 = cx / 2f
            y0 = 0f
            x1 = cx / 2f
            y1 = cy
        }
        GradientStartPosition.MIDDLE_BOTTOM -> {
            x0 = cx / 2f
            y0 = cy
            x1 = cx / 2f
            y1 = 0f
        }
    }
    return floatArrayOf(x0, y0, x1, y1)
}

enum class GradientStartPosition(val value: Int) {
    BOTTOM_LEFT(0),
    BOTTOM_RIGHT(1),
    TOP_LEFT(2),
    TOP_RIGHT(3),
    MIDDLE_LEFT(4),
    MIDDLE_RIGHT(5),
    MIDDLE_TOP(6),
    MIDDLE_BOTTOM(7),
}