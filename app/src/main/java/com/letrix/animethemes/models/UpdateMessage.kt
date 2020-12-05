package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable

data class UpdateMessage(
    val title: String?,
    val message: String?,
    val version: String?,
    val id: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(version)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdateMessage> {
        override fun createFromParcel(parcel: Parcel): UpdateMessage {
            return UpdateMessage(parcel)
        }

        override fun newArray(size: Int): Array<UpdateMessage?> {
            return arrayOfNulls(size)
        }
    }

}
