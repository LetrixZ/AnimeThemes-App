package com.letrix.animethemes.interfaces

import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.Theme

interface HomeListener {
    fun onAnimeClick(anime: Anime)
    fun onArtistClick(artist: Artist)
    fun onThemeClick(themeList: List<Theme>, position: Int)
    fun onYearClick(year: Int)

    fun onAnimeLongClick(anime: Anime)
    fun onArtistLongClick(artist: Artist)
}