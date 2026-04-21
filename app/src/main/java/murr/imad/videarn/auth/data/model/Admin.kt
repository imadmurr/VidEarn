package murr.imad.videarn.auth.data.model

import android.os.Parcel
import android.os.Parcelable

// START
data class Admin(
    val id: String = "",
    val name: String = "admin",
    val email: String = "",
    val fcmToken: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(fcmToken)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Admin> = object : Parcelable.Creator<Admin> {
            override fun createFromParcel(source: Parcel): Admin = Admin(source)
            override fun newArray(size: Int): Array<Admin?> = arrayOfNulls(size)
        }
    }
}
// END