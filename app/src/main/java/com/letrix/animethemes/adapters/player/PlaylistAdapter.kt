package com.letrix.animethemes.adapters.player

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.kotlin.interfaces.PlayerListener
import com.letrix.animethemes.models.Playlist
import com.letrix.animethemes.models.PlaylistItem
import com.letrix.animethemes.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_theme_large.view.*

class PlaylistAdapter(private val playlist: Playlist, private val playerListener: PlayerListener) :
    RecyclerView.Adapter<PlaylistAdapter.ItemHolder>() {

    private var selected = playlist.last
    private var last = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_theme_large, parent, false), selected, playerListener
        )
    }

    override fun getItemCount(): Int {
        return playlist.size()!!
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(playlist[position], position)
    }

    fun updatePos(position: Int) {
        last = selected
        if (position != last) {
            selected = position
            notifyItemChanged(selected)
            notifyItemChanged(last)
        }
    }

    class ItemHolder(
        itemView: View,
        private val selected: Int,
        private val playerListener: PlayerListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        @SuppressLint("SetTextI18n")
        fun bind(playlistItem: PlaylistItem, position: Int) {
            itemView.anime_title.text = playlistItem.name
            itemView.theme_title.text = playlistItem.title
            itemView.setOnClickListener(this)
            itemView.theme_type_quality.text =
                "${Utils.parseType(playlistItem.type!!)} ${
                    Utils.parseQuality(
                        playlistItem.mirror?.quality!!,
                        itemView.context,
                        1
                    )
                }"
            Picasso.get().load(playlistItem.cover).into(itemView.anime_cover)
            if (position == selected) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) itemView.apply {
                    theme_title.setTextAppearance(R.style.Played)
                } else itemView.apply {
                    TextViewCompat.setTextAppearance(theme_title, R.style.Played)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) itemView.apply {
                    theme_title.setTextAppearance(R.style.NotPlayed)
                } else itemView.apply {
                    TextViewCompat.setTextAppearance(theme_title, R.style.NotPlayed)
                }
            }
        }

        override fun onClick(p0: View?) {
            playerListener.onThemeClick(layoutPosition)
        }
    }
}