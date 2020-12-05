package com.letrix.animethemes.utils

import android.app.Activity
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.letrix.animethemes.models.BookmarkWrapper
import com.letrix.animethemes.models.Playlist
import timber.log.Timber.d


object SavedData {

    fun savePlaylist(playlist: Playlist, activity: Activity) {
        val prefEditor = PreferenceManager.getDefaultSharedPreferences(activity).edit()
        val jsonString = Gson().toJson(playlist)
        prefEditor.putString("playlist", jsonString).apply()
    }

    fun getPlaylist(activity: Activity): Playlist {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val jsonString = preferences.getString("playlist", null)
        return if (jsonString != null)
            Gson().fromJson(jsonString, Playlist::class.java)
        else Playlist("Your Playlist")
    }

    fun saveBookmarks(bookmarks: List<BookmarkWrapper>, activity: Activity) {
        val prefEditor = PreferenceManager.getDefaultSharedPreferences(activity).edit()
        val jsonString = Gson().toJson(bookmarks)
        prefEditor.putString("bookmarks", jsonString).apply()
    }

    fun getBookmarks(activity: Activity): ArrayList<BookmarkWrapper> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val jsonString = preferences.getString("bookmarks", null)
        val jsonType = object : TypeToken<ArrayList<BookmarkWrapper>>() {}.type
        return if (jsonString != null)
            Gson().fromJson(jsonString, jsonType)
        else ArrayList()
    }


    fun saveLastUpdate(lastUpdate: Int, activity: Activity) {
        val prefEditor = PreferenceManager.getDefaultSharedPreferences(activity).edit()
        val jsonString = Gson().toJson(lastUpdate)
        prefEditor.putString("s", jsonString).apply()
    }

    fun getLastUpdate(activity: Activity): ArrayList<BookmarkWrapper> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val jsonString = preferences.getString("bookmarks", null)
        val jsonType = object : TypeToken<ArrayList<BookmarkWrapper>>() {}.type
        return if (jsonString != null)
            Gson().fromJson(jsonString, jsonType)
        else ArrayList()
    }

}