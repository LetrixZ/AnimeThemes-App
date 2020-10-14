package com.letrix.animethemes.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionInflater
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.home.AnimeAdapter
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.data.MainViewModel
import com.letrix.animethemes.interfaces.HomeListener
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.Theme
import kotlinx.android.synthetic.main.alert_dialog_platform.view.*
import kotlinx.android.synthetic.main.fragment_userlist.*
import kotlinx.android.synthetic.main.fragment_userlist.progress_bar
import kotlinx.android.synthetic.main.fragment_userlist.search_rv
import kotlinx.android.synthetic.main.fragment_year.*
import kotlinx.android.synthetic.main.layout_header.*

class UserListFragment : Fragment(), HomeListener {

    private lateinit var dataViewModel: DataViewModel
    private lateinit var mainViewModel: MainViewModel

    lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewModel = ViewModelProvider(activity as MainActivity).get(DataViewModel::class.java)
        mainViewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_userlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion_layout_userlist.progress = dataViewModel.motionStates["userlist"] ?: 0f

        val userList = dataViewModel.userList
        if (userList != null) {
            initRecycler(userList.second)
            user_box.setText(userList.first)
        }

        inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        back_button.setOnClickListener { findNavController().popBackStack() }

        user_box.visibility = View.VISIBLE

        user_box.apply {
            setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_SEARCH) platformSelector(user_box.text.toString())
                true
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun platformSelector(user: String) {
        inputMethodManager.hideSoftInputFromWindow(
            search_box.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        val itemView = layoutInflater.inflate(R.layout.alert_dialog_platform, null)

        val builder =
            AlertDialog.Builder(requireActivity(), R.style.alert_dialog)
        val platformSelector = builder.create()
        platformSelector.apply {
            setCanceledOnTouchOutside(true)
            window?.setWindowAnimations(R.style.alert_dialog)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(itemView)
            setContentView(itemView)
        }.show()

        itemView.myAnimeList.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            search_rv.adapter = null
            mainViewModel.mal(user).observe(viewLifecycleOwner, Observer {
                if (it.isSuccessful) {
                    dataViewModel.userList = Pair(user, it.body()!!)
                    initRecycler(it.body()!!)
                }
            })
            platformSelector.dismiss()
        }

        itemView.aniList.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            search_rv.adapter = null
            mainViewModel.anilist(user).observe(viewLifecycleOwner, Observer {
                if (it.isSuccessful) {
                    dataViewModel.userList = Pair(user, it.body()!!)
                    initRecycler(it.body()!!)
                }
            })
            platformSelector.dismiss()
        }
    }

    private fun initRecycler(userList: List<Anime>) {
        progress_bar.visibility = View.GONE
        search_rv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 3)
            adapter = AnimeAdapter(userList, this@UserListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataViewModel.motionStates["userlist"] = motion_layout_userlist.progress
    }

    override fun onAnimeClick(anime: Anime) {
        dataViewModel.motionStates["info"] = 0f
        findNavController().navigate(
            UserListFragmentDirections.showInfo(
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