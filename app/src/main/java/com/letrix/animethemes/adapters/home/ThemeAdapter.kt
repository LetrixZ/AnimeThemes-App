package com.letrix.animethemes.adapters.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.Theme
import com.letrix.animethemes.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_theme_item_big.view.*

class ThemeAdapter(
    private val themeList: List<Theme>,
    private val homeListener: HomeListener
) :
    RecyclerView.Adapter<ThemeAdapter.ItemHolder>() {
    class ItemHolder(
        itemView: View,
        private val themeList: List<Theme>,
        private val homeListener: HomeListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        @SuppressLint("SetTextI18n")
        fun bind(theme: Theme) {
            itemView.theme_title.text = theme.title
            itemView.theme_type.text = "${Utils.parseType(theme.type)} | ${theme.views} " +
                    if (theme.views == 1) itemView.context.getString(R.string.view)
                    else itemView.context.getString(R.string.views)
            itemView.anime_title.text = theme.anime
            Picasso.get().load(theme.cover).into(itemView.anime_cover)
            itemView.clickable_layout.setOnClickListener(this)
        }

        override fun onClick(p0: View?) = homeListener.onThemeClick(
            themeList, layoutPosition
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_theme_item_small, parent, false),
        themeList,
        homeListener
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(themeList[position])

    override fun getItemCount(): Int = themeList.size
}