package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable

data class PlaylistItem(
    val anime: Anime,
    val theme: Theme,
    val mirror: Mirror
) : Parcelable {
    val notes: String = theme.notes
    val views: Int = theme.views
    val cover: String = anime.cover
    val name: String = anime.title
    val title: String = theme.title
    val type: String = theme.type

    constructor(anime: Anime, theme: Theme) : this(
        anime,
        theme,
        theme.mirrors.getOrElse(theme.selected) { theme.mirrors[0] }
    )

    constructor(theme: Theme) : this(
        Anime(malId = theme.malId, title = theme.anime, cover = theme.cover),
        theme,
        theme.mirrors[theme.selected]
    )

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Anime::class.java.classLoader)!!,
        parcel.readParcelable(Theme::class.java.classLoader)!!,
        parcel.readParcelable(Mirror::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(anime, flags)
        parcel.writeParcelable(theme, flags)
        parcel.writeParcelable(mirror, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlaylistItem> {
        override fun createFromParcel(parcel: Parcel): PlaylistItem {
            return PlaylistItem(parcel)
        }

        override fun newArray(size: Int): Array<PlaylistItem?> {
            return arrayOfNulls(size)
        }
    }
}
