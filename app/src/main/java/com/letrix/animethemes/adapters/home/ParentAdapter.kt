package com.letrix.animethemes.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.requests.HomeList
import kotlinx.android.synthetic.main.recycler_list_item.view.*

class ParentAdapter(private val homeList: HomeList, private val homeListener: HomeListener) :
    RecyclerView.Adapter<ParentAdapter.ItemHolder>() {

    val bookmarkAnimeAdapter = BookmarkAdapter(homeList.bookmarks, homeListener)

    class ItemHolder(
        itemView: View,
        private val homeList: HomeList,
        private val homeListener: HomeListener,
        private val bookmarkAnimeAdapter: BookmarkAdapter
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            when (position) {
                0 -> {
                    itemView.list_label.text = itemView.context.getString(R.string.Bookmarks)
                    itemView.list_rv.apply {
                        layoutManager =
                            LinearLayoutManager(
                                itemView.context,
                                RecyclerView.HORIZONTAL,
                                false
                            )
                        adapter = bookmarkAnimeAdapter
                    }
                    if (homeList.bookmarks.isEmpty()) itemView.list_label.visibility = View.GONE
                }
                2 -> {
                    itemView.list_label.text = itemView.context.getString(
                        R.string.current_season,
                        homeList.currentSeason[0].season
                    )
                    itemView.list_rv.apply {
                        layoutManager = GridLayoutManager(itemView.context, 3)
                        adapter = AnimeAdapter(homeList.currentSeason, homeListener)
                    }
                }
                1 -> {
                    itemView.list_label.text = itemView.context.getString(R.string.year)
                    itemView.list_rv.apply {
                        layoutManager =
                            LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
                        adapter = YearAdapter(homeList.yearList, homeListener)
                    }
                }
                3 -> {
                    itemView.list_label.text = itemView.context.getString(R.string.latest_anime)
                    itemView.list_rv.apply {
                        layoutManager = GridLayoutManager(itemView.context, 3)
                        adapter = AnimeAdapter(homeList.animeList, homeListener)
                    }
                }
                4 -> {
                    itemView.list_label.text = itemView.context.getString(R.string.latest_artist)
                    itemView.list_rv.apply {
                        layoutManager = GridLayoutManager(itemView.context, 3)
                        adapter = ArtistAdapter(homeList.artistList, homeListener)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false),
        homeList,
        homeListener,
        bookmarkAnimeAdapter
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = 5
}