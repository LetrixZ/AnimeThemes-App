package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable

data class BookmarkWrapper(
    val anime: Anime?,
    val artist: Artist?
) : Parcelable {
    constructor(anime: Anime?) : this(
        anime,
        null
    )

    constructor(artist: Artist?) : this(
        null,
        artist
    )

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Anime::class.java.classLoader),
        parcel.readParcelable(Artist::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(anime, flags)
        parcel.writeParcelable(artist, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookmarkWrapper> {
        override fun createFromParcel(parcel: Parcel): BookmarkWrapper {
            return BookmarkWrapper(parcel)
        }

        override fun newArray(size: Int): Array<BookmarkWrapper?> {
            return arrayOfNulls(size)
        }
    }
}