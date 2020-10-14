package com.letrix.animethemes.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.search.ParentAdapter
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.data.MainViewModel
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.*
import com.letrix.animethemes.models.requests.SearchList
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_header.*

class SearchFragment : Fragment(), HomeListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var searchList: SearchList

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion_layout_search.progress = dataViewModel.motionStates["search"] ?: 0f

        search_box.visibility = View.VISIBLE

        back_button.setOnClickListener { findNavController().popBackStack() }
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (dataViewModel.searchList != null) {
            initRecycler(dataViewModel.searchList!!)
            search_box.setText(if (dataViewModel.searchTerm != null) dataViewModel.searchTerm else "")
        }

        search_box.setOnEditorActionListener { _, i, _ ->
            search_box.clearFocus()
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                search_rv.adapter = null
                progress_bar.visibility = View.VISIBLE
                inputMethodManager.hideSoftInputFromWindow(
                    search_box.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                dataViewModel.searchTerm = search_box.text.toString()
                mainViewModel.search(search_box.text.toString())
                    .observe(viewLifecycleOwner, {
                        if (it.isSuccessful) it.body()?.let { list -> initRecycler(list) }
                    })
                true
            } else false
        }
    }

    private fun initRecycler(searchList: SearchList) {
        this.searchList = searchList
        dataViewModel.searchList = searchList
        progress_bar.visibility = View.GONE
        search_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ParentAdapter(searchList, this@SearchFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataViewModel.motionStates["search"] = motion_layout_search.progress
    }

    override fun onAnimeClick(anime: Anime) {
        dataViewModel.motionStates["info"] = 0f
        findNavController().navigate(
            SearchFragmentDirections.showInfo(
                anime.malId.toString(),
                "anime"
            )
        )
    }

    override fun onArtistClick(artist: Artist) {
        dataViewModel.motionStates["info"] = 0f
        findNavController().navigate(
            SearchFragmentDirections.showInfo(
                artist.malId.toString(), "artist"
            )
        )
    }

    override fun onThemeClick(themeList: List<Theme>, position: Int) {
        val playlist = Playlist(PlaylistItem(themeList[position]))
        playlist.last = position
        findNavController().navigate(SearchFragmentDirections.goPlay(playlist))
    }

    override fun onYearClick(year: Int) {
        // NOT USED
    }

    override fun onAnimeLongClick(anime: Anime) {
        (activity as MainActivity).bookmark(anime)
    }

    override fun onArtistLongClick(artist: Artist) {
        (activity as MainActivity).bookmark(artist)
    }
}