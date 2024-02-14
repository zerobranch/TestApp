package com.github.zerobranch.beebox.commons_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StyleRes

class ShadowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private companion object {
        const val TOP = 1
        const val BOTTOM = 2
        const val VERTICAL = TOP or BOTTOM
    }

    private val topShadowOffset: Float
    private val bottomShadowOffset: Float
    private val shadowDirection: Int

    private val shadowBlurRadius: Float
    private val rectRadius: Float
    private val shadowColor: Int
    private var topRectF: RectF? = null
    private var bottomRectF: RectF? = null
    private val topShadowPaint: Paint
    private val bottomShadowPaint: Paint

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ShadowView, defStyleAttr, defStyleRes)
        val offset = typedArray.getDimension(R.styleable.ShadowView_shadowOffset, 20.dp.toFloat())

        shadowDirection = typedArray.getInteger(R.styleable.ShadowView_shadowDirection, BOTTOM)
        shadowBlurRadius =
            typedArray.getDimension(R.styleable.ShadowView_shadowBlurRadius, 10.dp.toFloat())
        rectRadius = typedArray.getDimension(R.styleable.ShadowView_rectRadius, 0F)
        shadowColor = typedArray.getColor(R.styleable.ShadowView_shadowColor, Color.BLACK)
        typedArray.recycle()

        topShadowOffset = offset
        bottomShadowOffset = -offset

        topShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            setShadowLayer(shadowBlurRadius, 0f, -topShadowOffset / 2f, shadowColor)
        }

        bottomShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            setShadowLayer(shadowBlurRadius, 0f, -bottomShadowOffset / 2f, shadowColor)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        when (shadowDirection) {
            TOP -> {
                val rectTop = Rect()
                getDrawingRect(rectTop)
                topRectF = RectF(rectTop).apply { offset(0f, topShadowOffset) }
            }
            BOTTOM -> {
                val bottomRect = Rect()
                getDrawingRect(bottomRect)
                bottomRectF = RectF(bottomRect).apply { offset(0f, bottomShadowOffset) }
            }
            VERTICAL -> {
                val rectTop = Rect()
                getDrawingRect(rectTop)
                topRectF = RectF(rectTop).apply {
                    top += topShadowOffset
                    bottom = h / 2F
                }

                val bottomRect = Rect()
                getDrawingRect(bottomRect)
                bottomRectF = RectF(bottomRect).apply {
                    top = h / 2F
                    bottom += bottomShadowOffset
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        topRectF?.let { canvas.drawRoundRect(it, rectRadius, rectRadius, topShadowPaint) }
        bottomRectF?.let { canvas.drawRoundRect(it, rectRadius, rectRadius, bottomShadowPaint) }
    }
}