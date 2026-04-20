package murr.imad.myapplication.auth.data.model

import android.os.Parcel
import android.os.Parcelable

// TODO (Step 12: Create a User Model using parcelable.)
// START
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val points: Long = 0L,
    val fcmToken: String = "",
    val inviteCode : String = "",
    val redeemDialogShowed : Boolean = false,
    val dailyBonusTime: Long = 0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeLong(points)
        parcel.writeString(fcmToken)
        parcel.writeString(inviteCode)
        parcel.writeByte(if (redeemDialogShowed) 1 else 0)
        parcel.writeLong(dailyBonusTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

// END