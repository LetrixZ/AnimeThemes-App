package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Mirror(
    @SerializedName("mirror")
    val mirror: String,
    @SerializedName("audio")
    val audio: String,
    @SerializedName("quality")
    val quality: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mirror)
        parcel.writeString(audio)
        parcel.writeString(quality)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mirror> {
        override fun createFromParcel(parcel: Parcel): Mirror {
            return Mirror(parcel)
        }

        override fun newArray(size: Int): Array<Mirror?> {
            return arrayOfNulls(size)
        }
    }
}
