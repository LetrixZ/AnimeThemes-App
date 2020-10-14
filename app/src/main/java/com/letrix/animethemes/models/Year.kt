package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable

data class Year(
    val year: Int,
    val seasons: List<Season>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createTypedArrayList(Season)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(year)
        parcel.writeTypedList(seasons)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Year> {
        override fun createFromParcel(parcel: Parcel): Year {
            return Year(parcel)
        }

        override fun newArray(size: Int): Array<Year?> {
            return arrayOfNulls(size)
        }

    }

    data class Season(
        val season: String,
        val animes: List<Anime>
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.createTypedArrayList(Anime)!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(season)
            parcel.writeTypedList(animes)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Season> {
            override fun createFromParcel(parcel: Parcel): Season {
                return Season(parcel)
            }

            override fun newArray(size: Int): Array<Season?> {
                return arrayOfNulls(size)
            }
        }
    }
}
