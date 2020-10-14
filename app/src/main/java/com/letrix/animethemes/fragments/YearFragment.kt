package com.letrix.animethemes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.year.ParentAdapter
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.data.MainViewModel
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.Theme
import com.letrix.animethemes.models.Year
import kotlinx.android.synthetic.main.fragment_year.*
import kotlinx.android.synthetic.main.layout_header.*

class YearFragment : Fragment(), HomeListener {

    private val args: YearFragmentArgs by navArgs()
    private lateinit var dataViewModel: DataViewModel
    private lateinit var mainViewModel: MainViewModel

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
        return inflater.inflate(R.layout.fragment_year, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion_layout_year.progress = dataViewModel.motionStates["year"] ?: 0f

        val year = args.year
        val item: Year? = dataViewModel.yearList[year]
        if (item != null) {
            initRecycler(item)
        } else {
            progress_bar.visibility = View.VISIBLE
            mainViewModel.year(year).observe(viewLifecycleOwner, {
                if (it.isSuccessful) {
                    dataViewModel.yearList[year] = it.body()!!
                    initRecycler(it.body()!!)
                }
            })
        }

        back_button.setOnClickListener { findNavController().popBackStack() }

    }

    private fun initRecycler(year: Year) {
        progress_bar.visibility = View.GONE
        search_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ParentAdapter(year, this@YearFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataViewModel.motionStates["year"] = motion_layout_year.progress
    }

    override fun onAnimeClick(anime: Anime) {
        dataViewModel.motionStates["info"] = 0f
        findNavController().navigate(
            YearFragmentDirections.showInfo(
                anime.malId.toString(),
                "anime"
            )
        )
    }

    override fun onArtistClick(artist: Artist) {
        // NOT USED
    }

    override fun onThemeClick(themeList: List<Theme>, position: Int) {
        // NOT USED
    }

    override fun onYearClick(year: Int) {
        // NOT USED
    }

    override fun onAnimeLongClick(anime: Anime) {
        (activity as MainActivity).bookmark(anime)
    }

    override fun onArtistLongClick(artist: Artist) {
        // NOT USED
    }

}