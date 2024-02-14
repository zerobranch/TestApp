package com.github.zerobranch.beebox.commons_java.ext

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.File

fun File.zip(password: String): File {
    fun getZipFile(file: File): File {
        val parent = file.parent
        requireNotNull(parent) { "Error getting zip file" }
        return File(parent + File.separator + file.nameWithoutExtension + ".zip")
    }

    val parameters = ZipParameters().apply {
        compressionLevel = CompressionLevel.NORMAL
        compressionMethod = CompressionMethod.DEFLATE
        isEncryptFiles = true
        encryptionMethod = EncryptionMethod.AES
        aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
    }

    val destinationZipFile = getZipFile(this)
    val zipFile = ZipFile(destinationZipFile, password.toCharArray())

    if (this.isFile) {
        zipFile.addFile(this, parameters)
    } else if (this.isDirectory) {
        zipFile.addFolder(this, parameters)
    }

    zipFile.close()
    return zipFile.file
}

fun File.extractAll(password: String? = null, destination: File): File {
    val file = ZipFile(this, password?.toCharArray())
    file.extractAll(destination.absolutePath)
    file.close()
    return destination
}