package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("BlockingMethodInNonBlockingContext")
class ImageInputDelegate(
    private val imageCacheDir: String = IMAGE_CACHE_DIR,
    private val maxAvailableImageSize: Int = MAX_AVAILABLE_IMAGE_SIZE,
    private val cropImageWidth: Int = CROP_IMAGE_WIDTH,
) {
    private companion object {
        const val CROP_IMAGE_WIDTH = 700
        const val MAX_AVAILABLE_IMAGE_SIZE = 1024  // kb
        const val IMAGE_CACHE_DIR = "temp_images"
    }

    private val File.kbSize
        get() = length() / 1024

    suspend fun fileFromCameraOrNull(context: Context, result: ActivityResult): File? {
        val btm = result.data?.extras?.get("data") as? Bitmap ?: return null
        return btm.save(context)
    }

    suspend fun fileFromMediaStoreOrNull(context: Context, result: ActivityResult): File? {
        val uri = result.data?.data ?: return null

        val btm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        return btm?.save(context)
    }

    private suspend fun Bitmap.save(context: Context): File = withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, imageCacheDir)
        directory.deleteRecursively()
        directory.mkdir()
        val tmpFile = File.createTempFile("image", ".png", directory)
        this@save.write(tmpFile)

        if (tmpFile.kbSize > maxAvailableImageSize) {
            tmpFile.delete()
            ThumbnailUtils
                .extractThumbnail(this@save, cropImageWidth, cropImageWidth)
                .write(tmpFile)
        }
        return@withContext tmpFile
    }

    private fun Bitmap.write(path: File) {
        path.outputStream().buffered().use { this.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
