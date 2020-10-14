package com.letrix.animethemes.kotlin.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.models.Playlist
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target

class DescriptionAdapter(var playlist: Playlist, var context: Context) :
    MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): String {
        return "${playlist.last().title} (${playlist.last().type})"
    }

    override fun getCurrentContentText(player: Player): String? {
        return playlist.last().name
    }

    override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap? {
        val bitmap2 = arrayOfNulls<Bitmap>(1)
        Picasso.get().load(playlist.last().cover)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    bitmap2[0] = bitmap
                }

                override fun onBitmapFailed(
                    e: Exception,
                    errorDrawable: Drawable
                ) {
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            })
        return bitmap2[0]
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.putExtra("player", true)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

}