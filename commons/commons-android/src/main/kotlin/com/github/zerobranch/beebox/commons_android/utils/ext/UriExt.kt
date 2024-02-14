package com.github.zerobranch.beebox.commons_android.utils.ext

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.fragment.app.Fragment

context(Fragment)
val Uri.bitmap: Bitmap
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, this))
    } else {
        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, this)
    }