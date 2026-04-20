 package murr.imad.myapplication.internetsharing.ui.view.utils.network

import kotlin.math.roundToLong

fun main(){
    val trafficDifferenceBytes = 50
    val trafficDifferenceMB = trafficDifferenceBytes / 1024L
    val trafficDifferenceGB = trafficDifferenceMB / 1024L

    //println("Traffic B: $trafficDifferenceBytes")
    //println("Traffic MB: $trafficDifferenceMB")
    // println("Traffic GB: $trafficDifferenceGB")
    //println("Traffic Points: ${getPointsToReward(1240000000000)}")
}
 fun getPointsToReward(traffic: Long): Long {
     val kilobyte = 1024.0  // Binary units
     val megabyte = kilobyte * 1024.0
     val scalingFactor = 0.9765625 // Adjust this to balance the point system

     // Calculate points based on total traffic in MB
     val points = traffic / megabyte  // Get the value in MB (binary)

     return if (points > 1024) {
         (points * scalingFactor).roundToLong()  // Apply scaling factor for traffic > 1GB
     } else {
         points.roundToLong()  // No scaling factor for traffic <= 1GB
     }
 }


 /**
  * Format data with precision
  *
  * @param sent
  * @param received
  * @return
  */
 fun formatDataWithPrecision(sent: Long, received: Long): List<String> {
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
         totalData = String.format("%.3f", totalGB) + " GB"  // 3 decimal places for GB
     } else {
         totalData = String.format("%.2f", totalMB) + " MB"
     }

     if (sentMB > 1024) {
         sentGB = sentMB / 1024f
         sentData = String.format("%.3f", sentGB) + " GB"  // 3 decimal places for GB
     } else {
         sentData = String.format("%.2f", sentMB) + " MB"
     }

     if (receivedMB > 1024) {
         receivedGB = receivedMB / 1024f
         receivedData = String.format("%.3f", receivedGB) + " GB"  // 3 decimal places for GB
     } else {
         receivedData = String.format("%.2f", receivedMB) + " MB"
     }

     data = listOf(sentData, receivedData, totalData)
     return data
 }






