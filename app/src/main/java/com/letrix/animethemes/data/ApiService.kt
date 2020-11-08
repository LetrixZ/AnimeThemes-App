package com.letrix.animethemes.data

import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.Theme
import com.letrix.animethemes.models.Year
import com.letrix.animethemes.models.requests.HomeList
import com.letrix.animethemes.models.requests.SearchList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET(API + "home")
    suspend fun homeList(): Response<HomeList>

    @GET(API + "anime/{mal_id}")
    suspend fun anime(@Path("mal_id") malId: Int): Response<Anime>

    @GET(API + "artist/{mal_id}")
    suspend fun artist(@Path("mal_id") malId: Int): Response<Artist>

    @GET(API + "theme/{theme_id}")
    suspend fun theme(@Path("theme_id") themeId: String): Response<Theme>

    @GET(API + "search/{term}")
    suspend fun search(@Path("term") searchTerm: String): Response<SearchList>

    @GET(API + "year/{year}")
    suspend fun year(@Path("year") year: Int): Response<Year>

    @GET(API + "mal/{user}")
    suspend fun mal(@Path("user") user: String): Response<List<Anime>>

    @GET(API + "anilist/{user}")
    suspend fun anilist(@Path("user") user: String): Response<List<Anime>>

    companion object {
        const val API = "api/app/"
    }

}