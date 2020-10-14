package com.letrix.animethemes.models.requests

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.Theme
import java.util.*

data class SearchList(
    @SerializedName("anime_list")
    val animeList: ArrayList<Anime>?,
    @SerializedName("theme_list")
    val themeList: ArrayList<Theme>?,
    @SerializedName("artist_list")
    val artistList: ArrayList<Artist>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Anime),
        parcel.createTypedArrayList(Theme),
        parcel.createTypedArrayList(Artist)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(animeList)
        parcel.writeTypedList(themeList)
        parcel.writeTypedList(artistList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchList> {
        override fun createFromParcel(parcel: Parcel): SearchList {
            return SearchList(parcel)
        }

        override fun newArray(size: Int): Array<SearchList?> {
            return arrayOfNulls(size)
        }
    }
}