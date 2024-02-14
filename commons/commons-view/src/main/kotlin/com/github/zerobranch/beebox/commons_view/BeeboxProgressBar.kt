package com.github.zerobranch.beebox.commons_view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.StyleRes
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import com.github.zerobranch.beebox.commons_view.databinding.BeeboxProgressBarLayoutBinding
import com.google.android.material.progressindicator.CircularProgressIndicator

class BeeboxProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private companion object {
        const val DEFAULT_WRAPPER_ALPHA = 0.6F

        val SMALL_INDICATOR_SIZE = 25.dp
        val LARGE_INDICATOR_SIZE = 55.dp

        val SMALL_TRACK_THICKNESS = 5.dp
        val LARGE_TRACK_THICKNESS = 6.dp
    }

    private val binding = BeeboxProgressBarLayoutBinding.inflate(LayoutInflater.from(context), this)

    private var show = true
    private var isShowWrapper = true
    var size = Size.SMALL
        set(value) {
            field = value
            binding.beeboxProgressBarLoader.initSize()
        }

    private var showMode = ShowMode.SCALE
    private var baseColor = BaseColor.BLUE
    private var customBaseColor = Color.WHITE
    private var wrapperAlpha: Float = DEFAULT_WRAPPER_ALPHA

    init {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.BeeboxProgressBar,
                defStyleAttr,
                defStyleRes
            )
        show = typedArray.getBoolean(R.styleable.BeeboxProgressBar_show, show)
        isShowWrapper =
            typedArray.getBoolean(R.styleable.BeeboxProgressBar_showWrapper, isShowWrapper)
        size = typedArray.getEnum(R.styleable.BeeboxProgressBar_size, size)
        showMode = typedArray.getEnum(R.styleable.BeeboxProgressBar_showMode, showMode)
        wrapperAlpha =
            typedArray.getFloat(R.styleable.BeeboxProgressBar_wrapperAlpha, wrapperAlpha)
        baseColor = typedArray.getEnum(R.styleable.BeeboxProgressBar_baseColor, baseColor)
        customBaseColor =
            typedArray.getColor(R.styleable.BeeboxProgressBar_customBaseColor, customBaseColor)
        typedArray.recycle()

        initLoader()
    }

    fun reset() {
        binding.beeboxProgressBarLoader.isIndeterminate = false
    }

    fun refreshing() {
        binding.beeboxProgressBarLoader.isIndeterminate = true
    }

    fun releaseToRefresh() {
        binding.beeboxProgressBarLoader.isIndeterminate = false
    }

    fun pullProgress(pullDistance: Float, pullProgress: Float) {
        binding.beeboxProgressBarLoader.progress = (pullProgress * 100).toInt()
    }

    fun show() {
        when (showMode) {
            ShowMode.NONE -> binding.beeboxProgressBarLoader.show()
            ShowMode.SCALE -> binding.beeboxProgressBarLoader.expand()
        }

        if (isShowWrapper) {
            showWrapper()
        }
    }

    fun hide() {
        when (showMode) {
            ShowMode.NONE -> binding.beeboxProgressBarLoader.isVisible = false
            ShowMode.SCALE -> binding.beeboxProgressBarLoader.collapse()
        }

        hideWrapper()
    }

    fun hideWrapper() {
        setBackgroundColor(Color.TRANSPARENT)

        isFocusable = false
        isClickable = false
    }

    private fun showWrapper() {
        setBackgroundColor(
            transparentColor(
                Color.BLUE,
                wrapperAlpha
            )
        )

        isFocusable = true
        isClickable = true
    }

    private fun initLoader() {
        binding.beeboxProgressBarLoader.initSize()
        binding.beeboxProgressBarLoader.initBaseColor()
        binding.beeboxProgressBarLoader.initVisibility()

        if (show && isShowWrapper) {
            showWrapper()
        } else {
            hideWrapper()
        }
    }

    private fun CircularProgressIndicator.initVisibility() {
        when (showMode) {
            ShowMode.NONE -> isVisible = show
            ShowMode.SCALE -> {
                if (show) {
                    expand()
                } else {
                    collapse()
                }
            }
        }
    }

    private fun CircularProgressIndicator.initBaseColor() {
        val trackColorAlpha = 76 // 30%
        val indicatorColor = when (baseColor) {
            BaseColor.WHITE -> Color.WHITE
            BaseColor.BLUE -> Color.BLUE
            BaseColor.CUSTOM -> customBaseColor
        }

        trackColor = ColorUtils.setAlphaComponent(indicatorColor, trackColorAlpha)
        setIndicatorColor(indicatorColor)
    }

    private fun CircularProgressIndicator.initSize() {
        when (size) {
            Size.SMALL -> {
                indicatorSize = SMALL_INDICATOR_SIZE
                trackThickness = SMALL_TRACK_THICKNESS
            }
            Size.LARGE -> {
                indicatorSize = LARGE_INDICATOR_SIZE
                trackThickness = LARGE_TRACK_THICKNESS
            }
        }
    }

    private enum class BaseColor {
        WHITE,
        BLUE,
        CUSTOM,
    }

    enum class ShowMode {
        NONE,
        SCALE
    }

    enum class Size {
        SMALL,
        LARGE
    }
}
