package com.letrix.animethemes.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.utils.Utils
import kotlinx.android.synthetic.main.recycler_year_item.view.*

class YearAdapter(private val yearList: List<Int>, private val homeListener: HomeListener) :
    RecyclerView.Adapter<YearAdapter.ItemHolder>() {
    class ItemHolder(
        itemView: View,
        private val yearList: List<Int>,
        private val homeListener: HomeListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        fun bind(year: Int) {
            itemView.year_text.text = year.toString()
            itemView.item_background.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    Utils.getYearBackground(
                        layoutPosition
                    )
                )
            )
            itemView.clickable_layout.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            homeListener.onYearClick(yearList[layoutPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_year_item, parent, false),
        yearList,
        homeListener
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(yearList[position])

    override fun getItemCount(): Int = yearList.size
}