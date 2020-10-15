package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class Theme(
    @SerializedName("theme_id")
    val themeId: String,
    @SerializedName("name")
    val anime: String,
    @SerializedName("mal_id")
    val malId: Int = 0,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("views")
    val views: Int,
    @SerializedName("notes")
    val notes: String,
    @SerializedName("artist")
    val artist: String,
    @SerializedName("episodes")
    val episodes: String,
    @SerializedName("mirrors")
    val mirrors: ArrayList<Mirror> = ArrayList()
) : Parcelable {

    var selected = 0

    constructor(
        themeId: String,
        title: String,
        artist: String,
        views: Int,
        type: String,
        notes: String,
        episodes: String
    ) : this(
        themeId,
        "",
        0,
        "",
        title,
        type,
        views,
        notes,
        episodes,
        artist,
    )

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Mirror)!!
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(themeId)
        parcel.writeString(anime)
        parcel.writeInt(malId)
        parcel.writeString(cover)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeInt(views)
        parcel.writeString(notes)
        parcel.writeString(artist)
        parcel.writeString(episodes)
        parcel.writeTypedList(mirrors)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Theme> {
        override fun createFromParcel(parcel: Parcel): Theme {
            return Theme(parcel)
        }

        override fun newArray(size: Int): Array<Theme?> {
            return arrayOfNulls(size)
        }
    }
}