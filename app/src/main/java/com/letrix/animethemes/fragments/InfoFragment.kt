package com.letrix.animethemes.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.info.anime.ThemeAdapter
import com.letrix.animethemes.adapters.info.artist.ParentAdapter
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.data.MainViewModel
import com.letrix.animethemes.interfaces.ThemeListener
import com.letrix.animethemes.models.*
import com.letrix.animethemes.utils.Download
import com.letrix.animethemes.utils.Utils
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.alert_dialog_download.view.*
import kotlinx.android.synthetic.main.alert_dialog_mirror.view.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.layout_header.*
import timber.log.Timber.d

class InfoFragment : Fragment(), ThemeListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var themeAdapter: ThemeAdapter

    private val args: InfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val inflater = TransitionInflater.from(requireContext())
//        enterTransition = inflater.inflateTransition(R.transition.slide_right)
//        exitTransition = inflater.inflateTransition(R.transition.fade)
        mainViewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
        dataViewModel = ViewModelProvider(activity as MainActivity).get(DataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion_layout_info.progress = dataViewModel.motionStates["info"] ?: 0f

        val id = args.id
        val type = args.type

        when (type) {
            "anime" -> {
                val item: Anime? = dataViewModel.animeList[id.toInt()]
                if (item != null) {
                    initAnime(item)
                } else {
                    info_progress.visibility = View.VISIBLE
                    mainViewModel.anime(id.toInt()).observe(viewLifecycleOwner, {
                        if (it.isSuccessful) {
                            info_progress.visibility = View.GONE
                            val anime = it.body()!!
                            dataViewModel.animeList[anime.malId] = anime
                            initAnime(anime)
                        }
                    })
                }
            }
            "artist" -> {
                val item: Artist? = dataViewModel.artistList[id.toInt()]
                if (item != null) {
                    initArtist(item)
                } else {
                    info_progress.visibility = View.VISIBLE
                    mainViewModel.artist(id.toInt()).observe(viewLifecycleOwner, {
                        if (it.isSuccessful) {
                            info_progress.visibility = View.GONE
                            val artist = it.body()!!
                            dataViewModel.artistList[artist.malId] = artist
                            initArtist(artist)
                        }
                    })
                }
            }
        }

        back_button.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initAnime(anime: Anime) {
        Picasso.get().load(anime.cover)
            .transform(BlurTransformation(activity, 2, 2)).into(anime_cover)
        anime_title.text = anime.title
        info_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            themeAdapter = ThemeAdapter(anime.themes, this@InfoFragment, anime)
            adapter = themeAdapter
        }
    }

    private fun initArtist(artist: Artist) {
        Picasso.get().load(artist.cover)
            .transform(BlurTransformation(activity, 2, 2)).into(anime_cover)
        anime_title.text = artist.name
        info_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ParentAdapter(artist, this@InfoFragment)
        }
    }

    @SuppressLint("InflateParams")
    private fun mirrorSelector(theme: Theme, themeAdapter: ThemeAdapter, position: Int) {
        val itemView = layoutInflater.inflate(R.layout.alert_dialog_mirror, null)
        val mirrors = theme.mirrors
        d(theme.mirrors.size.toString())
        when (theme.mirrors.size) {
            1 -> {
                itemView.apply {
                    mirror_1.text =
                        Utils.parseQuality(mirrors[0].quality, activity as MainActivity, 0)
                }
            }
            2 -> {
                itemView.apply {
                    mirror_1.text =
                        Utils.parseQuality(mirrors[0].quality, activity as MainActivity, 0)
                    divider_1.visibility = View.VISIBLE
                    mirror_2.visibility = View.VISIBLE
                    mirror_2.text =
                        Utils.parseQuality(mirrors[1].quality, activity as MainActivity, 0)
                }
            }
            3 -> {
                itemView.apply {
                    mirror_1.text =
                        Utils.parseQuality(mirrors[0].quality, activity as MainActivity, 0)
                    divider_1.visibility = View.VISIBLE
                    mirror_2.visibility = View.VISIBLE
                    mirror_2.text =
                        Utils.parseQuality(mirrors[1].quality, activity as MainActivity, 0)
                    divider_2.visibility = View.VISIBLE
                    mirror_3.visibility = View.VISIBLE
                    mirror_3.text =
                        Utils.parseQuality(mirrors[2].quality, activity as MainActivity, 0)
                }
            }
        }

        val builder =
            AlertDialog.Builder(requireActivity(), R.style.alert_dialog)
        val themeContext = builder.create()
        themeContext.apply {
            setCanceledOnTouchOutside(true)
            window?.setWindowAnimations(R.style.alert_dialog)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(itemView)
            setContentView(itemView)
        }.show()

        itemView.mirror_1.setOnClickListener {
            theme.selected = 0
            themeAdapter.notifyItemChanged(position)
            themeContext.dismiss()
        }

        itemView.mirror_2.setOnClickListener {
            theme.selected = 1
            themeAdapter.notifyItemChanged(position)
            themeContext.dismiss()
        }

        itemView.mirror_3.setOnClickListener {
            theme.selected = 2
            themeAdapter.notifyItemChanged(position)
            themeContext.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun downloadSelector(theme: Theme, anime: Anime) {
        val itemView = layoutInflater.inflate(R.layout.alert_dialog_download, null)

        val builder =
            AlertDialog.Builder(requireActivity(), R.style.alert_dialog)
        val themeContext = builder.create()
        themeContext.apply {
            setCanceledOnTouchOutside(true)
            window?.setWindowAnimations(R.style.alert_dialog)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(itemView)
            setContentView(itemView)
        }.show()

        itemView.video_option.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) Download.beginDownload(
                    PlaylistItem(anime, theme),
                    activity as MainActivity,
                    "webm",
                    Uri.parse(theme.mirrors[theme.selected].mirror)
                ) else requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
            } else Download.beginDownload(
                PlaylistItem(anime, theme),
                activity as MainActivity,
                "webm",
                Uri.parse(theme.mirrors[theme.selected].mirror)
            )
            themeContext.dismiss()
        }

        itemView.audio_option.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) Download.beginDownload(
                    PlaylistItem(anime, theme),
                    activity as MainActivity,
                    "mp3",
                    Uri.parse(theme.mirrors[theme.selected].audio)
                ) else requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
            } else Download.beginDownload(
                PlaylistItem(anime, theme),
                activity as MainActivity,
                "mp3",
                Uri.parse(theme.mirrors[theme.selected].audio)
            )
            themeContext.dismiss()
        }
    }

    private fun playTheme(position: Int, anime: Anime) {
        val playlist = Playlist(anime.title)
        anime.themes.forEach {
            playlist.items.add(PlaylistItem(anime, it))
        }
        playlist.last = position
        playlist[position] = PlaylistItem(anime, anime[position])
        findNavController().navigate(InfoFragmentDirections.goPlay(playlist))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataViewModel.motionStates["info"] = motion_layout_info.progress
    }

    override fun onPlay(theme: Theme, anime: Anime) {
        playTheme(anime.themes.indexOf(theme), anime)
    }

    override fun onDownload(theme: Theme, anime: Anime, type: Int) =
        downloadSelector(theme, anime)

    //    override fun onDownload(theme: Theme, anime: Anime, type: Int) =
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) Download.beginDownload(
//                PlaylistItem(anime, theme),
//                activity as MainActivity
//            ) else requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
//        } else Download.beginDownload(PlaylistItem(anime, theme), activity as MainActivity)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                requireActivity(),
                "Permission granted. Click download again to start.",
                Toast.LENGTH_SHORT
            ).show() else Toast.makeText(
                requireActivity(),
                "Please give permission to download the file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onAdd(playlistItem: PlaylistItem) {
        (activity as MainActivity).addItem(playlistItem)
    }

    override fun onQuality(theme: Theme, themeAdapter: ThemeAdapter, position: Int) {
        mirrorSelector(theme, themeAdapter, position)
    }

}