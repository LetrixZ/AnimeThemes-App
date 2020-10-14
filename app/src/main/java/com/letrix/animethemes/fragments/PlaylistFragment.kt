package com.letrix.animethemes.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.playlist.PlaylistAdapter
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.interfaces.PlaylistListener
import com.letrix.animethemes.models.Playlist
import com.letrix.animethemes.models.PlaylistItem
import com.letrix.animethemes.utils.Download
import com.letrix.animethemes.utils.Utils
import com.letrix.animethemes.utils.Utils.textAsBitmap
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.layout_header.*
import timber.log.Timber.d
import java.util.*

class PlaylistFragment : Fragment(), PlaylistListener {

    private lateinit var dataViewModel: DataViewModel
    private lateinit var playlist: Playlist
    private lateinit var playlistAdapter: PlaylistAdapter
    private val playlistSize = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewModel = ViewModelProvider(activity as MainActivity).get(DataViewModel::class.java)
//        val inflater = TransitionInflater.from(requireContext())
//        enterTransition = inflater.inflateTransition(R.transition.slide_right)
//        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion_layout_playlist.progress = dataViewModel.motionStates["playlist"] ?: 0f

        playlist = dataViewModel.userPlaylist
        playlistSize.value = playlist.size()

        playlist_name.apply {
            visibility = View.VISIBLE
            text = playlist.name
        }

        recycler_view.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(activity)
            playlistAdapter = PlaylistAdapter(playlist, this@PlaylistFragment)
            adapter = playlistAdapter
        }

        back_button.setOnClickListener { findNavController().popBackStack() }

        play_fab.apply {
            setImageBitmap(
                textAsBitmap(
                    if (playlist.empty()) "0" else (playlist.last + 1).toString(),
                    32f,
                    Color.WHITE
                )
            )
            setOnClickListener {
                recycler_view.smoothScrollToPosition(playlist.last)
            }
            setOnLongClickListener {
                Toast.makeText(requireActivity(), "Last theme", Toast.LENGTH_SHORT).show()
                true
            }
        }

        clear_all_fab.apply {
            setOnClickListener { clearList() }
            setOnLongClickListener {
                Toast.makeText(requireActivity(), "Clear playlist", Toast.LENGTH_SHORT).show()
                true
            }
        }

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val positionDragged = dragged.layoutPosition
                    val positionTarget = target.layoutPosition
                    Collections.swap(playlist.items, positionDragged, positionTarget)
                    playlistAdapter.notifyItemMoved(positionDragged, positionTarget)
                    if (positionDragged == playlist.last) {
                        playlist.last = positionTarget
                    } else if (positionTarget == playlist.last) {
                        playlist.last = positionDragged
                    }
                    Utils.vibrate(requireActivity())
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.layoutPosition
                    Toast.makeText(
                        requireActivity(),
                        String.format(
                            "%s %s",
                            playlist[position].title,
                            resources.getString(R.string.deleted)
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    //Remove swiped item from list and notify the RecyclerView
                    playlist.delAt(position)
                    if (playlist.last == position) {
                        if (position != 0) {
                            d("position != 0")
                            playlist.last = position - 1
                        } else {
                            d("else")
                            playlist.last = position
                        }
                    } else if (position < playlist.last) {
                        d("position < playlist.last")
                        playlist.last = position
                    }
                    playlistAdapter.notifyItemRemoved(position)
                }

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    val swipeFlags = ItemTouchHelper.LEFT
                    return ItemTouchHelper.Callback.makeMovementFlags(
                        dragFlags,
                        swipeFlags
                    )
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    play_fab.setImageBitmap(
                        textAsBitmap((playlist.last + 1).toString(), 32f, Color.WHITE)
                    )
                    playlistSize.value = playlist.size()
                }

                override fun onMoved(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    fromPos: Int,
                    target: RecyclerView.ViewHolder,
                    toPos: Int,
                    x: Int,
                    y: Int
                ) {
                    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
                    playlistAdapter.notifyDataSetChanged()
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)

        playlistSize.observe(viewLifecycleOwner, {
            when (it) {
                in 0..6 -> {
                    if (motion_layout_playlist.progress == 0f)
                        motion_layout_playlist.enableTransition(R.id.def, false)
                }
                else -> {
                    motion_layout_playlist.enableTransition(R.id.def, true)
                }
            }
            if (it == 0) empty_text.visibility = View.VISIBLE else empty_text.visibility = View.GONE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataViewModel.motionStates["playlist"] = motion_layout_playlist.progress
    }

    private fun clearList() {
        val size = playlist.size()
        playlist.clear()
        playlistSize.value = 0
        playlistAdapter.notifyItemRangeRemoved(0, size)
        empty_text.visibility = View.VISIBLE
        play_fab.setImageBitmap(textAsBitmap((0).toString(), 32f, Color.WHITE))
    }

    override fun onThemeClick(position: Int) {
        playlist.last = position
        playlistAdapter.updatePos(position)
        findNavController().navigate(PlaylistFragmentDirections.goPlay(playlist))
    }

    override fun onThemeDownload(playlistItem: PlaylistItem) = if (Build.VERSION.SDK_INT >= 23) {
        if (activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) Download.beginDownload(
            playlistItem,
            activity as MainActivity
        ) else requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
    } else Download.beginDownload(playlistItem, activity as MainActivity)

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

}