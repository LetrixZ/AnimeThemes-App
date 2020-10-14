package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

data class Anime(
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("year")
    val year: Int,
    @SerializedName("season")
    val season: String,
    @SerializedName("themes")
    @Nullable
    val themes: ArrayList<Theme> = ArrayList()
) : Parcelable {



    constructor(malId: Int, title: String, cover: String) : this(
        malId,
        title,
        cover,
        0,
        ""
    )

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.createTypedArrayList(Theme)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(malId)
        parcel.writeString(title)
        parcel.writeString(cover)
        parcel.writeInt(year)
        parcel.writeString(season)
        parcel.writeTypedList(themes)
    }

    override fun describeContents(): Int {
        return 0
    }

    operator fun get(position: Int): Theme = themes[position]
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Anime

        if (malId != other.malId) return false
        if (title != other.title) return false
        if (cover != other.cover) return false

        return true
    }

    override fun hashCode(): Int {
        var result = malId
        result = 31 * result + title.hashCode()
        result = 31 * result + cover.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<Anime> {
        override fun createFromParcel(parcel: Parcel): Anime {
            return Anime(parcel)
        }

        override fun newArray(size: Int): Array<Anime?> {
            return arrayOfNulls(size)
        }
    }
}