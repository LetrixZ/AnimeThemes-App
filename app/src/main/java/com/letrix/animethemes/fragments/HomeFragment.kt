package com.letrix.animethemes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.home.ParentAdapter
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.*
import com.letrix.animethemes.models.requests.HomeList
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), HomeListener {

    private lateinit var homeList: HomeList
    private lateinit var dataViewModel: DataViewModel
    lateinit var parentAdapter: ParentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val inflater = TransitionInflater.from(requireContext())
//        exitTransition = inflater.inflateTransition(R.transition.fade)
        dataViewModel = ViewModelProvider(activity as MainActivity).get(DataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion_layout.progress = dataViewModel.motionStates["home"] ?: 0f

        arguments?.getParcelable<HomeList>("homeList")?.let {
            homeList = it
            initRecyclerView(homeList)
        }

        search_button.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.goSearch())
        }

        playlist_button.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.toPlaylist())
        }

        userlist_button.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.userList())
        }

    }

    private fun initRecyclerView(homeList: HomeList) {
        home_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            parentAdapter = ParentAdapter(homeList, this@HomeFragment)
            dataViewModel.parentAdapter = parentAdapter
            adapter = parentAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataViewModel.motionStates["home"] = motion_layout.progress
    }

    override fun onAnimeClick(anime: Anime) {
        dataViewModel.motionStates["info"] = 0f
        findNavController().navigate(
            HomeFragmentDirections.showInfo(
                anime.malId.toString(),
                "anime"
            )
        )
    }

    override fun onArtistClick(artist: Artist) {
        dataViewModel.motionStates["info"] = 0f
        findNavController().navigate(
            HomeFragmentDirections.showInfo(
                artist.malId.toString(),
                "artist"
            )
        )
    }

    override fun onThemeClick(themeList: List<Theme>, position: Int) {
        val playlist =
            Playlist(themeList.map { PlaylistItem(it) } as ArrayList<PlaylistItem>, "Top List")
        playlist.last = position
        findNavController().navigate(HomeFragmentDirections.goPlay(playlist))
    }

    override fun onYearClick(year: Int) {
        dataViewModel.motionStates["year"] = 0f
        findNavController().navigate(
            HomeFragmentDirections.getYear(year)
        )
    }

    override fun onAnimeLongClick(anime: Anime) {
        (activity as MainActivity).bookmark(anime)
    }

    override fun onArtistLongClick(artist: Artist) {
        (activity as MainActivity).bookmark(artist)
    }
}