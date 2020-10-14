package com.letrix.animethemes.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.models.PlaylistItem

object Download {

    fun beginDownload(item: PlaylistItem, activity: Activity) {
        lateinit var request: DownloadManager.Request
        val fileName =
            "${item.title} ${Utils.parseQuality(
                item.mirror.quality,
                activity,
                2
            )} - ${item.name} (${item.type}).webm"
        request = DownloadManager.Request(Uri.parse(item.mirror.mirror)).apply {
            setTitle(fileName)
            setDescription("Downloading theme")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MOVIES,
                "AnimeThemes/${fileName}"
            )
        }
        val downloadManager =
            activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        (activity as MainActivity).downloadId = downloadManager.enqueue(request)
        Toast.makeText(activity, "Download started", Toast.LENGTH_SHORT).show()
    }

}