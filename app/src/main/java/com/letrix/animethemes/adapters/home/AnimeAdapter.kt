package com.letrix.animethemes.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.Anime
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_anime_item_big.view.anime_cover
import kotlinx.android.synthetic.main.recycler_anime_item_big.view.anime_title
import kotlinx.android.synthetic.main.recycler_anime_item_mid.view.*

class AnimeAdapter(private val animeList: List<Anime>, private val homeListener: HomeListener) :
    RecyclerView.Adapter<AnimeAdapter.ItemHolder>() {
    class ItemHolder(itemView: View, private val homeListener: HomeListener, private val animeList: List<Anime>) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        fun bind(anime: Anime) {
            itemView.anime_title.text = anime.title
            itemView.anime_cover.transitionName = anime.malId.toString()
            Picasso.get().load(anime.cover).into(itemView.anime_cover)
            itemView.clickable_item.setOnClickListener(this)
            itemView.clickable_item.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            homeListener.onAnimeClick(animeList[layoutPosition])
        }

        override fun onLongClick(p0: View?): Boolean {
            homeListener.onAnimeLongClick(animeList[layoutPosition])
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_anime_item_circle_mid, parent, false),
        homeListener,
        animeList
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(animeList[position])

    override fun getItemCount(): Int = animeList.size
}