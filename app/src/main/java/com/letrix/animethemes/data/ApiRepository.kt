package com.letrix.animethemes.data

import android.content.Context
import com.letrix.animethemes.data.ApiService
import com.letrix.animethemes.data.RetrofitBuilder
import com.letrix.animethemes.models.*
import com.letrix.animethemes.models.requests.HomeList
import com.letrix.animethemes.models.requests.SearchList
import retrofit2.Response

class ApiRepository(context: Context) {

    var client: ApiService = RetrofitBuilder(context).apiService

    suspend fun homeList(): Response<HomeList> = client.homeList()

    suspend fun getUpdates(): Response<UpdateMessage> = client.getUpdates()

    suspend fun anime(malId: Int): Response<Anime> = client.anime(malId)
    suspend fun artist(malId: Int): Response<Artist> = client.artist(malId)
    suspend fun theme(themeId: String): Response<Theme> = client.theme(themeId)

    suspend fun search(searchTerm: String): Response<SearchList> = client.search(searchTerm)

    suspend fun year(year: Int): Response<Year> = client.year(year)

    suspend fun mal(user: String): Response<List<Anime>> = client.mal(user)
    suspend fun anilist(user: String): Response<List<Anime>> = client.anilist(user)

}