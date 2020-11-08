package com.letrix.animethemes.adapters.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.BookmarkWrapper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rcl_anime_square.view.*

class BookmarkAdapter(
    private val bookmarks: ArrayList<BookmarkWrapper>,
    private val homeListener: HomeListener
) :
    RecyclerView.Adapter<BookmarkAdapter.ItemHolder>() {
    class ItemHolder(
        itemView: View,
        private val homeListener: HomeListener,
        private val bookmarks: ArrayList<BookmarkWrapper>
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        fun bind(bookmarkWrapper: BookmarkWrapper) {
            if (bookmarkWrapper.anime != null) {
                val anime = bookmarkWrapper.anime
                itemView.anime_title.text = anime.title
                itemView.anime_cover.transitionName = anime.malId.toString()
                Picasso.get().load(anime.cover).into(itemView.anime_cover)
                itemView.clickable_item.setOnClickListener(this)
                itemView.clickable_item.setOnLongClickListener(this)
            } else if (bookmarkWrapper.artist != null) {
                val artist = bookmarkWrapper.artist
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
        }

        override fun onClick(p0: View?) {
            val bookmarkWrapper = bookmarks[layoutPosition]
            if (bookmarkWrapper.anime != null) homeListener.onAnimeClick(bookmarkWrapper.anime)
            else if (bookmarkWrapper.artist != null) homeListener.onArtistClick(bookmarkWrapper.artist)
        }

        override fun onLongClick(p0: View?): Boolean {
            val bookmarkWrapper = bookmarks[layoutPosition]
            if (bookmarkWrapper.anime != null) homeListener.onAnimeLongClick(bookmarkWrapper.anime)
            else if (bookmarkWrapper.artist != null) homeListener.onArtistLongClick(bookmarkWrapper.artist)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context)
            .inflate(PreferenceManager.getDefaultSharedPreferences(parent.context).getInt("layoutViewNC", R.layout.rcl_anime_rect), parent, false),
        homeListener,
        bookmarks
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(bookmarks[position])

    override fun getItemCount(): Int = bookmarks.size
}