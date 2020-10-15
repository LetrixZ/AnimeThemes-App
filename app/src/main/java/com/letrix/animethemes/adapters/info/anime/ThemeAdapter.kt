package com.letrix.animethemes.adapters.info.anime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.animethemes.R
import com.letrix.animethemes.interfaces.ThemeListener
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.PlaylistItem
import com.letrix.animethemes.models.Theme
import com.letrix.animethemes.utils.Utils
import kotlinx.android.synthetic.main.recycler_theme_large_info.view.*
import kotlinx.android.synthetic.main.recycler_theme_large_mini.view.*
import kotlinx.android.synthetic.main.recycler_theme_large_mini.view.theme_title

class ThemeAdapter(
    private val themeList: List<Theme>,
    private val themeListener: ThemeListener,
    private val anime: Anime
) :
    RecyclerView.Adapter<ThemeAdapter.ItemHolder>() {
    class ItemHolder(
        itemView: View, private val anime: Anime,
        private val themeListener: ThemeListener,
        private val themeAdapter: ThemeAdapter
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        fun bind(theme: Theme) {
            itemView.theme_title.text = theme.title
//            itemView.theme_type.text =
//                if (theme.notes.isNotEmpty()) "${Utils.parseType(theme.type)} | ${theme.notes}" else Utils.parseType(
//                    theme.type
//                )
            itemView.theme_type.text = Utils.parseType(theme.type)
            if (theme.notes.isNotEmpty()) itemView.theme_type.append(" | ${theme.notes}")
            if (theme.episodes.isNotEmpty()) itemView.theme_type.append("\nEpisodes ${theme.episodes}")
            itemView.theme_quality.text =
                Utils.parseQuality(
                    theme.mirrors[theme.selected].quality,
                    itemView.context,
                    -1
                )
//            itemView.theme_quality.paintFlags = itemView.theme_quality.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            itemView.play_theme.setOnClickListener(this)
            itemView.download_video.setOnClickListener(this)
            itemView.add_to_playlist.setOnClickListener(this)
            itemView.theme_quality.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            when (p0) {
                itemView.play_theme -> themeListener.onPlay(anime.themes[layoutPosition], anime)
                itemView.download_video -> themeListener.onDownload(
                    anime.themes[layoutPosition],
                    anime,
                    0
                )
                itemView.theme_quality -> themeListener.onQuality(
                    anime.themes[layoutPosition],
                    themeAdapter,
                    layoutPosition
                )
                itemView.add_to_playlist -> themeListener.onAdd(
                    PlaylistItem(
                        anime,
                        anime.themes[layoutPosition]
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_theme_large_mini, parent, false),
        anime,
        themeListener,
        this
    )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.bind(themeList[position])

    override fun getItemCount(): Int = themeList.size
}