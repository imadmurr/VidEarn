package com.app.admin

object Constants {

    const val FCM_TOKEN: String = "fcmToken"
    const val VIDEARN_PREFERENCES: String = "shared preferences"
    const val FCM_TOKEN_UPDATED: String = "token Updated"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = ""
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    // This  is used for the collection name for USERS.
    const val USERS: String = "users"
    const val PAYMENTS : String = "payments"
    const val CRYPTO_PAYMENTS : String = "crypto"
    const val GIFT_CARDS_PAYMENTS : String = "gift-cards"

    // Firebase database field names
    const val POINTS: String = "points"
    const val NAME: String = "name"
    const val DIALOG_SHOWED_REDEEM : String = "redeemDialogShowed"
    var USER_UID : String = ""
    const val DAILY_BONUS_TIME : String = "dailyBonusTime"

    //Other constants
    const val PAYMENT_PENALTY : Long = 500L

    const val AD_REWARD = 20L
    const val REFERRAL_REWARD = 500L
    const val DAILY_BONUS = 50L
    const val BTC_PAYMENT_THRESHOLD = 100000L
    const val USDT_PAYMENT_THRESHOLD = 50000L
}