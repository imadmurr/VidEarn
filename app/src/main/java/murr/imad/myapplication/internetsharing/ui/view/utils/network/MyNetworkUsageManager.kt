package murr.imad.myapplication.internetsharing.ui.view.utils.network

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import dev.jahidhasanco.networkusage.NetworkType
import dev.jahidhasanco.networkusage.NetworkUsageManager
import dev.jahidhasanco.networkusage.TimeInterval
import dev.jahidhasanco.networkusage.Usage
import dev.jahidhasanco.networkusage.Util

class MyNetworkUsageManager(private val originalTracker: NetworkUsageManager,
                            private val context: Context
) {

    private var statsmanager = context.getSystemService(Context.NETWORK_STATS_SERVICE)
            as NetworkStatsManager

    @RequiresApi(Build.VERSION_CODES.P)
    fun getBackgroundUsageForMyApp(interval: TimeInterval, networkType: NetworkType): Usage {
        val stats = statsmanager.queryDetailsForUidTagState(
            when (networkType) {
                NetworkType.MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
                NetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                NetworkType.ALL -> NetworkCapabilities.TRANSPORT_WIFI and NetworkCapabilities.TRANSPORT_CELLULAR
            }, Util.getSubscriberId(context), interval.start, interval.end,
            context.applicationInfo.uid, NetworkStats.Bucket.TAG_NONE, NetworkStats.Bucket.STATE_DEFAULT
        )

        val bucket = NetworkStats.Bucket()
        val usage = Usage()
        usage.timeTaken = interval.end - interval.start
        if (stats != null) {
            val myAppUid = context.applicationInfo.uid
            //println("APP Id: $myAppUid")
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                //println("Bucket State: ${bucket.state}")
                // Check if the data usage is for your app's UID
                if (bucket.uid == myAppUid ) {
                    usage.downloads += bucket.rxBytes
                    usage.uploads += bucket.txBytes
                }
            }
        }

        stats.close()
        return usage
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getForegroundUsageForMyApp(interval: TimeInterval, networkType: NetworkType): Usage {
        val stats = statsmanager.queryDetailsForUidTagState(
            when (networkType) {
                NetworkType.MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
                NetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                NetworkType.ALL -> NetworkCapabilities.TRANSPORT_WIFI and NetworkCapabilities.TRANSPORT_CELLULAR
            }, Util.getSubscriberId(context), interval.start, interval.end,
            context.applicationInfo.uid, NetworkStats.Bucket.TAG_NONE, NetworkStats.Bucket.STATE_FOREGROUND
        )

        val bucket = NetworkStats.Bucket()
        val usage = Usage()
        usage.timeTaken = interval.end - interval.start
        if (stats != null) {
            val myAppUid = context.applicationInfo.uid
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                //println("Bucket State: ${bucket.state}")
                // Check if the data usage is for your app's UID
                if (bucket.uid == myAppUid ) {
                    usage.downloads += bucket.rxBytes
                    usage.uploads += bucket.txBytes
                }
            }
        }

        stats.close()
        return usage
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getTotalUsageForMyApp(interval: TimeInterval, networkType: NetworkType): Usage {
        val stats = statsmanager.queryDetailsForUidTagState(
            when (networkType) {
                NetworkType.MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
                NetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                NetworkType.ALL -> NetworkCapabilities.TRANSPORT_WIFI and NetworkCapabilities.TRANSPORT_CELLULAR
            }, Util.getSubscriberId(context), interval.start, interval.end,
            context.applicationInfo.uid, NetworkStats.Bucket.TAG_NONE, NetworkStats.Bucket.STATE_ALL
        )

        val bucket = NetworkStats.Bucket()
        val usage = Usage()
        usage.timeTaken = interval.end - interval.start
        if (stats != null) {
            val myAppUid = context.applicationInfo.uid
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                //println("Bucket State: ${bucket.state}")
                // Check if the data usage is for your app's UID
                if (bucket.uid == myAppUid ) {
                    usage.downloads += bucket.rxBytes
                    usage.uploads += bucket.txBytes
                }
            }
        }

        stats.close()
        return usage
    }

    fun getUploadUsageForMyApp(sent: Long, received: Long = 0L): List<String> {
        val total = sent + received
        val data: List<String>
        val totalBytes = total / 1024f
        val sentBytes = sent / 1024f
        val receivedBytes = received / 1024f
        val totalMB = totalBytes / 1024f
        val totalGB: Float
        val sentGB: Float
        val receivedGB: Float
        val sentMB: Float = sentBytes / 1024f
        val receivedMB: Float = receivedBytes / 1024f
        var sentData = ""
        var receivedData = ""
        val totalData: String
        if (totalMB > 1024) {
            totalGB = totalMB / 1024f
            totalData = String.format("%.2f", totalGB) + " GB"
        } else {
            totalData = String.format("%.2f", totalMB) + " MB"
        }
        if (sentMB > 1024) {
            sentGB = sentMB / 1024f
            sentData = String.format("%.2f", sentGB) + " GB"
        } else {
            sentData = String.format("%.2f", sentMB) + " MB"
        }
        if (receivedMB > 1024) {
            receivedGB = receivedMB / 1024f
            receivedData = String.format("%.2f", receivedGB) + " GB"
        } else {
            receivedData = String.format("%.2f", receivedMB) + " MB"
        }
        data = listOf(sentData, receivedData, totalData)
        return data
    }

    fun getDownloadUsageForMyApp(sent: Long = 0L, received: Long): List<String> {
        val total = sent + received
        val data: List<String>
        val totalBytes = total / 1024f
        val sentBytes = sent / 1024f
        val receivedBytes = received / 1024f
        val totalMB = totalBytes / 1024f
        val totalGB: Float
        val sentGB: Float
        val receivedGB: Float
        val sentMB: Float = sentBytes / 1024f
        val receivedMB: Float = receivedBytes / 1024f
        var sentData = ""
        var receivedData = ""
        val totalData: String
        if (totalMB > 1024) {
            totalGB = totalMB / 1024f
            totalData = String.format("%.2f", totalGB) + " GB"
        } else {
            totalData = String.format("%.2f", totalMB) + " MB"
        }
        if (sentMB > 1024) {
            sentGB = sentMB / 1024f
            sentData = String.format("%.2f", sentGB) + " GB"
        } else {
            sentData = String.format("%.2f", sentMB) + " MB"
        }
        if (receivedMB > 1024) {
            receivedGB = receivedMB / 1024f
            receivedData = String.format("%.2f", receivedGB) + " GB"
        } else {
            receivedData = String.format("%.2f", receivedMB) + " MB"
        }
        data = listOf(sentData, receivedData, totalData)
        return data
    }

}
