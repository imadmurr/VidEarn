package murr.imad.myapplication.internetsharing.ui.view.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

/**
 * Network monitor
 *
 * @constructor Create empty Network monitor
 */
object NetworkMonitor {

    private lateinit var connectivityManager: ConnectivityManager
    var networkType: String = ""
    private var networkChangeListener: ((String) -> Unit)? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    /**
     * Initialize
     *
     * @param context
     */
    fun initialize(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        startMonitoring()
    }

    /**
     * Start monitoring
     *
     */
    private fun startMonitoring() {
        val builder = NetworkRequest.Builder()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities?.let {
                    when {
                        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            Log.d("NetworkMonitor", "Connected to Wi-Fi")
                            networkType = "WiFi"
                        }
                        it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            Log.d("NetworkMonitor", "Connected to Mobile Data")
                            networkType = "Mobile Data"
                        }
                        else -> {
                            networkType = "No Connection"
                        }
                    }
                    networkChangeListener?.invoke(networkType)
                }
            }

            override fun onLost(network: android.net.Network) {
                Log.d("NetworkMonitor", "Connection Lost")
                networkType = "No Connection"
                networkChangeListener?.invoke(networkType)
            }
        }

        connectivityManager.registerNetworkCallback(builder.build(), networkCallback!!)
    }

    fun isConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    fun getConnectionType(): String {
        return networkType
    }

    /**
     * Set network change listener
     *
     * @param listener
     * @receiver
     */
    fun setNetworkChangeListener(listener: (String) -> Unit) {
        networkChangeListener = listener
    }

    /**
     * Stop monitoring
     *
     */
    fun stopMonitoring() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            Log.d("NetworkMonitor", "Network monitoring stopped")
        }
    }
}
