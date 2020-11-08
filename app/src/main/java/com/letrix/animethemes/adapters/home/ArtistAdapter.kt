package com.letrix.animethemes.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.Artist
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rcl_anime_rect.view.*

class ArtistAdapter(private val artistList: List<Artist>, private val homeListener: HomeListener) :
    RecyclerView.Adapter<ArtistAdapter.ItemHolder>() {
    class ItemHolder(
        itemView: View,
        private val homeListener: HomeListener,
        private val artistList: List<Artist>
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        fun bind(artist: Artist) {
            itemView.anime_title.text = artist.name
            itemView.clickable_item.setOnClickListener(this)
            itemView.clickable_item.setOnLongClickListener(this)
            if (artist.cover.isNotEmpty()) Picasso.get().load(artist.cover)
                .into(itemView.anime_cover)
            else itemView.anime_cover.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.color.appBackground
                )
            )
        }

        override fun onClick(p0: View?) {
            homeListener.onArtistClick(artistList[layoutPosition])
        }

        override fun onLongClick(p0: View?): Boolean {
            homeListener.onArtistLongClick(artistList[layoutPosition])
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context)
            .inflate(
                PreferenceManager.getDefaultSharedPreferences(parent.context)
                    .getInt("layoutView", R.layout.rcl_anime_rect_center), parent, false
            ),
        homeListener,
        artistList
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(artistList[position])

    override fun getItemCount(): Int = artistList.size
}