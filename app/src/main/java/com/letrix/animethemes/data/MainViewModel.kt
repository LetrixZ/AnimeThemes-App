package com.letrix.animethemes.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.Theme
import com.letrix.animethemes.models.Year
import com.letrix.animethemes.models.requests.SearchList
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ApiRepository =
        ApiRepository(application.applicationContext)

    val homeList = liveData(Dispatchers.IO) {
        val homeList = repository.homeList()
        emit(homeList)
    }

    fun anime(malId: Int): LiveData<Response<Anime>> {
        return liveData(Dispatchers.IO) {
            val anime = repository.anime(malId)
            emit(anime)
        }
    }

    fun artist(malId: Int): LiveData<Response<Artist>> {
        return liveData(Dispatchers.IO) {
            val artist = repository.artist(malId)
            emit(artist)
        }
    }

    fun theme(themeId: String): LiveData<Response<Theme>> {
        return liveData(Dispatchers.IO) {
            val theme = repository.theme(themeId)
            emit(theme)
        }
    }

    fun search(searchTerm: String): LiveData<Response<SearchList>> {
        return liveData(Dispatchers.IO) {
            val searchList = repository.search(searchTerm)
            emit(searchList)
        }
    }

    fun year(year: Int): LiveData<Response<Year>> {
        return liveData(Dispatchers.IO) {
            val yearList = repository.year(year)
            emit(yearList)
        }
    }

    fun mal(user: String): LiveData<Response<List<Anime>>> {
        return liveData(Dispatchers.IO) {
            val userList = repository.mal(user)
            emit(userList)
        }
    }

    fun anilist(user: String): LiveData<Response<List<Anime>>> {
        return liveData(Dispatchers.IO) {
            val userList = repository.anilist(user)
            emit(userList)
        }
    }

}