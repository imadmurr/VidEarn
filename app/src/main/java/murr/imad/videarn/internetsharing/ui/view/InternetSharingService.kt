package murr.imad.videarn.internetsharing.ui.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ServiceCompat
import com.iproyal.sdk.common.dto.ServiceState
import com.iproyal.sdk.common.dto.ServiceType
import com.iproyal.sdk.common.listener.PawnsServiceListener
import com.iproyal.sdk.common.sdk.Pawns
import com.traffmonetizer.sdk.TraffmonetizerSdk
import murr.imad.videarn.R
import murr.imad.videarn.internetsharing.ui.view.utils.permissions.PermissionUtil
import murr.imad.videarn.internetsharing.ui.view.utils.network.NetworkMonitor
import murr.imad.videarn.internetsharing.ui.view.utils.network.TrafficChecker

class InternetSharingService : Service(), PawnsServiceListener {

    //private lateinit var userId : String
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var trafficChecker: TrafficChecker

    /**
     * This method is called when the service is bound. In this case, it always returns null,
     * as there is no binding logic implemented in this service.
     * @param intent The Intent that was used to bind to this service.
     * @return Always returns null.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * This method is called when the service is started. It initializes and starts the Pawns SDK,
     * starts the TraffMonetizer SDK, creates a wake lock to prevent the service from being affected by Doze Mode,
     * and returns START_STICKY to indicate that the service should be restarted if the system kills it.
     * @param intent The Intent supplied to start this service.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service's current state.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Extract userId from the Intent
        //userId = intent?.getStringExtra("userId").toString()

        // Build and start the foreground notification
        val notification = buildForegroundNotification("")
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )
        // Start Pawns Sdk
        //Pawns.getInstance().startSharing(this)
        Pawns.getInstance().registerListener(this@InternetSharingService)

        // Start TraffMonetizer Sdk
        startTraffMonetizer()

        // Start the TrafficChecker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            trafficChecker.startTrafficChecking()
        }



        // Create a wake lock to prevent service from being affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        // Start the foreground service with a sticky notification
        return START_STICKY
    }

    /**
     * This method is called when the service is created. It initializes and starts the Pawns SDK's service
     * with a background service type and sets up a wake lock.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        // Create a notification for the foreground service
        createNotification()
        // Initialize and start the Pawns SDK's service
        Pawns.Builder(this@InternetSharingService)
            .apiKey("PAWNS_API_KEY")
            .serviceType(ServiceType.BACKGROUND) // Use BACKGROUND instead of FOREGROUND
            .build()
        //Initialize the traffic checker
        trafficChecker = TrafficChecker(this@InternetSharingService)

        //Network Monitor
        NetworkMonitor.initialize(this@InternetSharingService)
        // Set the network change listener
        NetworkMonitor.setNetworkChangeListener { networkType ->
            handleNetworkTypeChange(networkType)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNetworkTypeChange(networkType: String) {
        when (networkType) {
            "WiFi" -> {
                // Logic for when connected to WiFi
                //updateForegroundNotification("Wi-Fi Connected")
                Pawns.getInstance().startSharing(this)
            }
            "Mobile Data" -> {
                if (!PermissionUtil.isReadPhoneStatePermissionGranted(this)){
                    Pawns.getInstance().stopSharing(this)
                    updateForegroundNotification("Service stopped: Mobile data on, permission needed")
                } else {
                    Pawns.getInstance().startSharing(this)
                }
            }
            "No Connection" -> {
                updateForegroundNotification("No Internet Connection!")
                Pawns.getInstance().stopSharing(this)
            }
        }
    }

    /**
     * This method is called when the service is destroyed. It stops and unregisters the Pawns SDK,
     * stops the TraffMonetizer SDK, releases the wake lock, and stops the foreground service.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the PawnsServiceListener and stop the service
        Pawns.getInstance().unregisterListener()
        Pawns.getInstance().stopSharing(this)

        // Stop TraffMonetizer SDK
        stopTraffMonetizer()

        // Stop the TrafficChecker
        trafficChecker.stopTrafficChecking()

        NetworkMonitor.stopMonitoring()

        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Stop the foreground service
        @Suppress("DEPRECATION")
        stopForeground(true)
        stopSelf()
    }

    /**
     * Starts the TraffMonetizer SDK.
     */
    private fun startTraffMonetizer() {
        try {
            TraffmonetizerSdk.start()
            Log.e("TraffMonetizer", "SDK started")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stops the TraffMonetizer SDK.
     */
    private fun stopTraffMonetizer() {
        try {
            TraffmonetizerSdk.stop()
            Log.e("TraffMonetizer", "SDK stopped")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This method is called when there is a change in the state of the Pawns SDK's service.
     * It updates the foreground notification based on the service state.
     * @param state The new state of the Pawns SDK's service.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStateChange(state: ServiceState) {
        when (state) {
            ServiceState.On -> {
                // The Pawns SDK's service has started running
                updateForegroundNotification("Starting..")
            }
            ServiceState.Off -> {
                // The Pawns SDK's service has stopped
                updateForegroundNotification("Stopped, retrying..")
                // Your code for the task goes here
                //Pawns.getInstance().startSharing(this)
                //val networkType = NetworkMonitor.getConnectionType()
                //handleNetworkTypeChange(networkType)
            }
            ServiceState.Launched.Running -> {
                updateForegroundNotification("Running")
            }
            is ServiceState.Launched.Error -> {
                updateForegroundNotification(state.error.toString())
                Pawns.getInstance().stopSharing(this)
            }
            else -> {

            }
        }
    }

    /**
     * Creates a foreground notification for the service.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {
        // Check if the channel already exists
        val channelID = "Foreground Service ID"
        val notificationManager = getSystemService(NotificationManager::class.java)
        val existingChannel = notificationManager.getNotificationChannel(channelID)

        if (existingChannel == null) {
            // Create the channel if it doesn't exist
            val channel = NotificationChannel(
                channelID,
                channelID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds a foreground notification with the specified state.
     * @param state The state to be displayed in the notification.
     * @return A Notification object representing the foreground notification.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildForegroundNotification(state: String): Notification {
        // Check if the channel already exists
        val channelID = "Foreground Service ID"
        val notificationManager = getSystemService(NotificationManager::class.java)
        val existingChannel = notificationManager.getNotificationChannel(channelID)

        if (existingChannel == null) {
            // Create the channel if it doesn't exist
            val channel = NotificationChannel(
                channelID,
                channelID,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        return Notification.Builder(this, channelID)
            .setChannelId(channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Internet Sharing Service")
            .setContentText(state)
            .setAutoCancel(true)
            .build()
    }

    /**
     * Updates the foreground notification with the specified state.
     * @param state The new state to be displayed in the notification.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateForegroundNotification(state: String) {
        val notification = buildForegroundNotification(state)
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val NOTIFICATION_ID = 1
    }

}