package com.letrix.animethemes.adapters.year

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.home.AnimeAdapter
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.Year
import kotlinx.android.synthetic.main.recycler_list_item.view.*

class ParentAdapter(private val year: Year, private val homeListener: HomeListener) :
    RecyclerView.Adapter<ParentAdapter.ItemHolder>() {
    class ItemHolder(itemView: View, private val homeListener: HomeListener) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(season: Year.Season) {
            itemView.list_label.text = season.season
            itemView.list_rv.apply {
                layoutManager = GridLayoutManager(itemView.context, 3)
                adapter = AnimeAdapter(season.animes, homeListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false),
        homeListener
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(year.seasons[position])

    override fun getItemCount(): Int = year.seasons.size
}