package com.letrix.animethemes.models.requests

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.BookmarkWrapper
import com.letrix.animethemes.models.Theme

data class HomeList(
    @SerializedName("bookmark_list")
    var bookmarks: ArrayList<BookmarkWrapper>,
    @SerializedName("year_list")
    val yearList: List<Int>,
    @SerializedName("current_season")
    val currentSeason: List<Anime>,
    @SerializedName("latest_anime")
    val animeList: List<Anime>,
    @SerializedName("latest_artist")
    val artistList: List<Artist>
) : Parcelable {

    constructor(source: Parcel) : this(
        ArrayList<BookmarkWrapper>().apply {
            source.readList(
                this,
                ArrayList::class.java.classLoader
            )
        },
        ArrayList<Int>().apply { source.readList(this, Int::class.java.classLoader) },
        ArrayList<Anime>().apply { source.readList(this, Anime::class.java.classLoader) },
        ArrayList<Anime>().apply { source.readList(this, Anime::class.java.classLoader) },
        ArrayList<Artist>().apply { source.readList(this, Artist::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeList(bookmarks)
        writeList(yearList)
        writeList(currentSeason)
        writeList(animeList)
        writeList(artistList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HomeList> = object : Parcelable.Creator<HomeList> {
            override fun createFromParcel(source: Parcel): HomeList = HomeList(source)
            override fun newArray(size: Int): Array<HomeList?> = arrayOfNulls(size)
        }
    }
}