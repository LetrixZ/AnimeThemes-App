package com.letrix.animethemes.kotlin.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.letrix.animethemes.models.Playlist

object BuildMediaSource {
    private fun buildMediaSource(context: Context?, uri: Uri?): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(context, "exoplayer")
        return ProgressiveMediaSource.Factory(dataSourceFactory).setTag("theme")
            .createMediaSource(uri)
    }

    fun buildConcatenatingMediaSource(
        context: Context?,
        playlist: Playlist
    ): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        for (item in playlist.items!!) {
            concatenatingMediaSource.addMediaSource(
                buildMediaSource(
                    context,
                    Uri.parse(item.mirror?.mirror)
                )
            )
        }
        return concatenatingMediaSource
    }

}