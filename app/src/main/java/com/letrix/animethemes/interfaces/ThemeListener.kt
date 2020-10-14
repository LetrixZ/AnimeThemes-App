package com.letrix.animethemes.interfaces

import com.letrix.animethemes.adapters.info.anime.ThemeAdapter
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.PlaylistItem
import com.letrix.animethemes.models.Theme

interface ThemeListener {
    fun onPlay(theme: Theme, anime: Anime)
    fun onDownload(theme: Theme, anime: Anime, type: Int)
    fun onAdd(playlistItem: PlaylistItem)
    fun onQuality(theme: Theme, themeAdapter: ThemeAdapter, position: Int)
}