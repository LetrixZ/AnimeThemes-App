package com.letrix.animethemes.interfaces

import com.letrix.animethemes.models.PlaylistItem

interface PlaylistListener {
    fun onThemeClick(position: Int)
    fun onThemeDownload(playlistItem: PlaylistItem)
}