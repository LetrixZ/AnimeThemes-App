package com.letrix.animethemes.adapters.info.artist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.info.anime.ThemeAdapter
import com.letrix.animethemes.interfaces.ThemeListener
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.recycler_list_artist.view.*

class ParentAdapter(private val artist: Artist, private val themeListener: ThemeListener) :
    RecyclerView.Adapter<ParentAdapter.ItemHolder>() {

    class ItemHolder(
        itemView: View,
        private val themeListener: ThemeListener
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(anime: Anime) {
            itemView.anime_title.text = anime.title
            itemView.recycler_view.apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = ThemeAdapter(anime.themes, themeListener, anime)
            }
            Picasso.get().load(anime.cover)
                .transform(BlurTransformation(itemView.context, 3, 1))
                .into(itemView.anime_cover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_list_artist, parent, false
        ), themeListener
    )


    override fun getItemCount(): Int = artist.animeList.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int): Unit =
        holder.bind(artist.animeList[position])
}