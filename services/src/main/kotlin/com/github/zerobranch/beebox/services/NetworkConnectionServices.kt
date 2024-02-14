package com.github.zerobranch.beebox.services

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.github.zerobranch.beebox.logging.crash.CrashLogger
import com.github.zerobranch.beebox.logging.wtf
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.NetworkInterface
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("MissingPermission")
@Singleton
class NetworkConnectionServices @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        const val TAG = "NetworkConnectionServices"
    }

    private val availableNetworks = mutableMapOf<String, TransportType>()
    private var cm: ConnectivityManager? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            handleState(network, NetworkState.AVAILABLE)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            handleState(network, NetworkState.LOSING)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            handleState(network, NetworkState.LOST)
        }
    }

    fun launch() {
        runCatching {
            cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            cm?.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
        }.onFailure { th ->
            javaClass.wtf(
                TAG,
                th,
                "launch registerNetworkCallback failed"
            )
        }
    }

    private fun handleState(network: Network, networkState: NetworkState) =
        runCatching {
            var networkType = getNetworkType(network)

            if (networkState == NetworkState.AVAILABLE && networkType != null) {
                availableNetworks[network.toString()] = networkType
            } else if (networkType == null) {
                networkType = availableNetworks[network.toString()] ?: TransportType.UNKNOWN
            }

            if (networkState == NetworkState.LOST) {
                availableNetworks.remove(network.toString())
            }

            CrashLogger.onNetworkStateChanged(networkState.name, networkType.name, context.isWpnUp())
        }.onFailure { th ->
            javaClass.wtf(TAG, th, "handleState failed")
        }

    private fun getNetworkType(network: Network): TransportType? {
        val activeNetwork = cm?.getNetworkCapabilities(network) ?: return null

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> TransportType.BLUETOOTH
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> TransportType.MOBILE
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> TransportType.ETHERNET
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> TransportType.LOWPAN
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_USB) -> TransportType.USB
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> TransportType.VPN
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> TransportType.WIFI
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> TransportType.WIFI_AWARE
            else -> TransportType.OTHER
        }
    }

    private fun Context.isWpnUp(): Boolean? =
        runCatching {
            NetworkInterface
                .getNetworkInterfaces()
                .toList()
                .filter { network -> network.isUp }
                .map { network -> network.name }
                .contains("tun0")
        }.getOrNull()

    private enum class NetworkState {
        AVAILABLE,
        LOSING,
        LOST
    }

    private enum class TransportType {
        BLUETOOTH,
        MOBILE,
        ETHERNET,
        LOWPAN,
        USB,
        VPN,
        WIFI,
        WIFI_AWARE,
        OTHER,
        UNKNOWN
    }
}