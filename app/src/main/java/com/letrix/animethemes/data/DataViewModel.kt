package com.letrix.animethemes.data

import androidx.lifecycle.ViewModel
import com.letrix.animethemes.adapters.home.ParentAdapter
import com.letrix.animethemes.models.*
import com.letrix.animethemes.models.requests.HomeList
import com.letrix.animethemes.models.requests.SearchList

class DataViewModel : ViewModel() {

    lateinit var homeList: HomeList
    lateinit var userPlaylist: Playlist
    var parentAdapter: ParentAdapter? = null
    var userBookmarks: ArrayList<BookmarkWrapper> = ArrayList()

    var motionStates = HashMap<String, Float>()

    var searchList: SearchList? = null
    var searchTerm: String? = null

    var animeList: HashMap<Int, Anime> = HashMap()
    var artistList: HashMap<Int, Artist> = HashMap()

    var yearList: HashMap<Int, Year> = HashMap()

    var userList: Pair<String, List<Anime>>? = null

}