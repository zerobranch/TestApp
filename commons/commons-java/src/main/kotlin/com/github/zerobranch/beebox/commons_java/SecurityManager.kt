package com.github.zerobranch.beebox.commons_java

import java.math.BigInteger
import java.security.MessageDigest

val String.sha512: String
    get() {
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashtext: String = no.toString(16)

        while (hashtext.length < 128) {
            hashtext = "0$hashtext"
        }

        return hashtext
    }
