package com.github.zerobranch.beebox.commons_android.utils.listeners

import android.view.View

class CubeInRotationTransformation @JvmOverloads constructor(
    private val distanceMultiplier: Int = 35
) : ABaseTransformer() {
    public override val isPagingEnabled: Boolean
        get() = true

    override fun onTransform(page: View, position: Float) {
        page.cameraDistance = (page.width * distanceMultiplier).toFloat()
        page.pivotX = if (position < 0f) page.width.toFloat() else 0f
        page.pivotY = page.height * 0.5f
        page.rotationY = 90f * position
    }
}