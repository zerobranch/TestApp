package com.github.zerobranch.beebox.commons_android.utils.ext

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val trustAllCertificateFactory: Pair<SSLSocketFactory, TrustManager>
    get() {
        @SuppressLint("CustomX509TrustManager")
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        return sslContext.socketFactory to trustAllCerts[0]
    }

val OkHttpClient.Builder.trustAllCertificate: OkHttpClient.Builder
    get() = apply {
        val (sslSocketFactory, trustManager) = trustAllCertificateFactory
        sslSocketFactory(sslSocketFactory, trustManager as X509TrustManager)
        hostnameVerifier { _, _ -> true }
    }