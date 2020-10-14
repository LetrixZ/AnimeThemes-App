package com.letrix.animethemes.adapters.playlist

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.PlaylistListener
import com.letrix.animethemes.models.Playlist
import com.letrix.animethemes.models.PlaylistItem
import com.letrix.animethemes.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_theme_bg_large.view.*

class PlaylistAdapter(
    private val playlist: Playlist,
    private val playlistListener: PlaylistListener
) :
    RecyclerView.Adapter<PlaylistAdapter.ItemHolder>() {

    private var lastSelected = 0
    private var selectedItem = playlist.last

    @Suppress("DEPRECATION")
    class ItemHolder(
        itemView: View,
        private val playlist: Playlist,
        private val playlistListener: PlaylistListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        fun bind(item: PlaylistItem) {
            Picasso.get().load(item.cover).into(itemView.anime_cover)
            itemView.theme_title.text = item.title
            itemView.theme_quality.text =
                Utils.parseQuality(item.mirror.quality, itemView.context, -1)
            itemView.theme_type.text =
                if (item.notes.isNotEmpty()) "${item.name} (${Utils.parseType(item.type)} | ${item.notes})"
                else "${item.name} (${Utils.parseType(item.type)})"
            itemView.setOnClickListener(this)
            itemView.download_video.setOnClickListener(this)

            if (layoutPosition == playlist.last) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) itemView.apply {
                    theme_title.setTextAppearance(R.style.Played)
                    theme_type.setTextAppearance(R.style.Played)
                    theme_quality.setTextAppearance(R.style.Played)
                } else itemView.apply {
                    theme_title.setTextAppearance(context, R.style.Played)
                    theme_type.setTextAppearance(context, R.style.Played)
                    theme_quality.setTextAppearance(context, R.style.Played)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) itemView.apply {
                    theme_title.setTextAppearance(R.style.NotPlayed)
                    theme_type.setTextAppearance(R.style.NotPlayed)
                    theme_quality.setTextAppearance(R.style.NotPlayed)
                } else itemView.apply {
                    theme_title.setTextAppearance(context, R.style.NotPlayed)
                    theme_type.setTextAppearance(context, R.style.NotPlayed)
                    theme_quality.setTextAppearance(context, R.style.NotPlayed)
                }
            }

        }

        override fun onClick(p0: View?) {
            when (p0) {
                itemView -> playlistListener.onThemeClick(layoutPosition)
                itemView.download_video -> playlistListener.onThemeDownload(playlist[layoutPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_theme_bg_large, parent, false
        ), playlist, playlistListener
    )

    override fun getItemCount(): Int = playlist.size()

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(playlist[position])

    fun updatePos(position: Int) {
        lastSelected = selectedItem
        selectedItem = position
        notifyItemChanged(lastSelected)
        notifyItemChanged(selectedItem)
    }
}