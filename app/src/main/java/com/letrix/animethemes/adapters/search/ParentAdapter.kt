package com.letrix.animethemes.adapters.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.home.AnimeAdapter
import com.letrix.animethemes.adapters.home.ArtistAdapter
import com.letrix.animethemes.adapters.home.ThemeAdapter
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.requests.SearchList
import kotlinx.android.synthetic.main.recycler_list_item.view.*

class ParentAdapter(private val searchList: SearchList, private val homeListener: HomeListener) :
    RecyclerView.Adapter<ParentAdapter.ItemHolder>() {
    class ItemHolder(
        itemView: View,
        private val searchList: SearchList,
        private val homeListener: HomeListener
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            when (position) {
                0 -> {
                    if (searchList.animeList?.isNotEmpty()!!) {
                        itemView.list_label.text =
                            itemView.context.getString(R.string.anime)
                        itemView.list_rv.apply {
                            layoutManager = GridLayoutManager(itemView.context, 3)
                            adapter = AnimeAdapter(searchList.animeList, homeListener)
                        }
                    } else {
                        val layoutParameter = itemView.layoutParams
                        layoutParameter.height = 0
                        itemView.layoutParams = layoutParameter
                    }
                }
                1 -> {
                    if (searchList.themeList?.isNotEmpty()!!) {
                        itemView.list_label.text =
                            itemView.context.getString(R.string.themes)
                        itemView.list_rv.apply {
                            layoutManager =
                                LinearLayoutManager(
                                    itemView.context,
                                    RecyclerView.HORIZONTAL,
                                    false
                                )
                            adapter = ThemeAdapter(searchList.themeList, homeListener)
                        }
                    } else {
                        val layoutParameter = itemView.layoutParams
                        layoutParameter.height = 0
                        itemView.layoutParams = layoutParameter
                    }
                }
                2 -> {
                    if (searchList.artistList?.isNotEmpty()!!) {
                        itemView.list_label.text =
                            itemView.context.getString(R.string.artists)
                        itemView.list_rv.apply {
                            layoutManager = GridLayoutManager(itemView.context, 3)
                            adapter = ArtistAdapter(searchList.artistList, homeListener)
                        }
                    } else {
                        val layoutParameter = itemView.layoutParams
                        layoutParameter.height = 0
                        itemView.layoutParams = layoutParameter
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false),
        searchList,
        homeListener
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = 3
}