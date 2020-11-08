package com.letrix.animethemes.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.letrix.animethemes.R
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.layout_header.*
import java.util.*

class SettingsFragment : Fragment() {

    private val views: List<Int> = listOf(R.id.rect_opt, R.id.circle_opt, R.id.square_opt)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPref.edit()

        back_button.setOnClickListener { findNavController().popBackStack() }

        setSelected()

        rect_opt.setOnClickListener {
            editor.putInt("configView", R.id.rect_opt)
            editor.putInt("layoutView", R.layout.rcl_anime_rect_center).apply()
            editor.putInt("layoutViewNC", R.layout.rcl_anime_rect).apply()
            setSelected()
        }

        square_opt.setOnClickListener {
            editor.putInt("configView", R.id.square_opt)
            editor.putInt("layoutView", R.layout.rcl_anime_square_center).apply()
            editor.putInt("layoutViewNC", R.layout.rcl_anime_square).apply()
            setSelected()
        }

        circle_opt.setOnClickListener {
            editor.putInt("configView", R.id.circle_opt)
            editor.putInt("layoutView", R.layout.rcl_anime_round_center).apply()
            editor.putInt("layoutViewNC", R.layout.rcl_anime_rounded).apply()
            setSelected()
        }
    }

    private fun setSelected() {
        for (viewId in views) {
            view?.findViewById<CardView>(viewId)
                ?.setCardBackgroundColor(resources.getColor(R.color.secondaryBackground))
        }
        val selectedView: Int = PreferenceManager.getDefaultSharedPreferences(activity)
            .getInt("configView", R.id.rect_opt)
        view?.findViewById<CardView>(selectedView)
            ?.setCardBackgroundColor(resources.getColor(R.color.colorAccent))
    }

}