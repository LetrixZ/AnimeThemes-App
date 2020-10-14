package com.letrix.animethemes.data

import android.content.Context
import com.letrix.animethemes.kotlin.data.ApiService
import com.letrix.animethemes.utils.Utils
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder(private val context: Context) {

    private val BASE_URL: String = "https://animethemes-api.herokuapp.com/"
//    private val BASE_URL: String = "http://192.168.1.40:5000/"


    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(660, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiService: ApiService by lazy {
        retrofitBuilder
            .build()
            .create(ApiService::class.java)
    }


//    private val cacheSize = (5 * 1024 * 1024).toLong()
//    private val myCache = Cache(context.cacheDir, cacheSize)
//
//    private val okHttpClient = OkHttpClient.Builder()
//        .cache(myCache)
//        .addInterceptor { chain ->
//            var request = chain.request()
//            request = if (Utils.hasNetwork(context)!!)
//                request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
//            else
//                request.newBuilder().header(
//                    "Cache-Control",
//                    "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
//                ).build()
//            chain.proceed(request)
//        }
//        .build()
//
//    private val retrofitBuilder: Retrofit.Builder by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//    }
//
//    val apiService: ApiService by lazy {
//        retrofitBuilder
//            .build()
//            .create(ApiService::class.java)
//    }

}