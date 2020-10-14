package com.letrix.animethemes.models

import android.os.Parcel
import android.os.Parcelable

data class Playlist(
    val items: ArrayList<PlaylistItem> = ArrayList(),
    val name: String
) : Parcelable {
    var last = 0

    constructor(name: String) : this(
        ArrayList(),
        name
    )

    constructor(playlistItem: PlaylistItem) : this(arrayListOf(playlistItem), playlistItem.name)

    constructor(source: Parcel) : this(
        source.createTypedArrayList(PlaylistItem.CREATOR) as ArrayList<PlaylistItem>,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(items)
        writeString(name)
    }

    operator fun get(position: Int): PlaylistItem = items[position]
    fun size(): Int = items.size
    operator fun set(position: Int, value: PlaylistItem) = items[position]
    fun last() = items[last]
    fun empty(): Boolean = items.isEmpty()
    fun delAt(position: Int) {
        items.removeAt(position)
        if (items.isEmpty()) last = 0
    }

    fun clear() = items.clear()
    fun add(playlistItem: PlaylistItem) {
        last = 0
        items.add(playlistItem)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Playlist> = object : Parcelable.Creator<Playlist> {
            override fun createFromParcel(source: Parcel): Playlist = Playlist(source)
            override fun newArray(size: Int): Array<Playlist?> = arrayOfNulls(size)
        }
    }
}
