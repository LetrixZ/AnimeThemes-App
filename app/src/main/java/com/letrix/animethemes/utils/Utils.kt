package com.letrix.animethemes.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.letrix.animethemes.R
import hideSystemUI
import showSystemUI
import java.util.*
import kotlin.collections.ArrayList

object Utils {

    fun parseQuality(
        qualityParam: String,
        context: Context,
        type: Int
    ): String {
        if (qualityParam.isEmpty()) return when (type) {
            -1 -> qualityParam.replace("", context.getString(R.string.def))
            1 -> "| ${qualityParam.replace("", context.getString(R.string.def))}"
            2 -> "(${qualityParam.replace("", context.getString(R.string.def))})"
            else -> qualityParam.replace("", context.getString(R.string.def))
        }
        var quality = qualityParam
        quality = quality
            .replace("Trans", context.getString(R.string.trans))
            .replace("Over", context.getString(R.string.over))
            .replace("Lyrics", context.getString(R.string.lyrics))
            .replace("Subbed", context.getString(R.string.subbed))
            .replace("Uncen", context.getString(R.string.uncen))
            .replace("NC", context.getString(R.string.no_credits))
        return when (type) {
            1 -> "| $quality"
            2 -> "($quality)"
            else -> quality
        }
    }

    //    fun parseType(type: String?) : String? = type?.replace("ED", "Ending ")?.replace("OP", "Opening ")
    fun parseType(text: String): String {
        var type = text
        type = type.substring(0, 2)
        type = type.replace("ED", "Ending")
        type = type.replace("OP", "Opening")
        if (text.length > 2) type += " " + text.substring(2)
        return type
    }

    fun getYearBackground(position: Int): Int {
        val bgList: List<Int> = ArrayList(
            Arrays.asList(
                R.drawable.bna_2020,
                R.drawable.yaiba_2019,
                R.drawable.hero_2018,
                R.drawable.magus_2017,
                R.drawable.assassination_2016,
                R.drawable.punch_2015,
                R.drawable.ngnl_2014,
                R.drawable.snk_2013,
                R.drawable.psycho_2012,
                R.drawable.steins_2011,
                R.drawable.angel_2010,
                R.drawable.metal_2009,
                R.drawable.eater_2008,
                R.drawable.naruto_2007,
                R.drawable.gintama_2006,
                R.drawable.gunxsword_2005,
                R.drawable.monster_2004,
                R.drawable.kinos_2003,
                R.drawable.complex_2002,
                R.drawable.hellsing_2001,
                R.drawable.boogiepop_2000,
                R.drawable.evangelion_90,
                R.drawable.dbz_80,
                R.drawable.maginzer_70,
                R.drawable.astro_60
            )
        )
        return bgList[position]
    }

    fun parseSeason(text: String?, context: Context): String? {
        return text!!.replace("Summer", context.resources.getString(R.string.summer))
            .replace("Fall", context.resources.getString(R.string.fall))
            .replace("Winter", context.resources.getString(R.string.winter))
            .replace("Spring", context.resources.getString(R.string.spring))
    }


    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Check if no view has focus
        val currentFocusedView = activity.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }

    fun vibrate(context: Context) =
        if (Build.VERSION.SDK_INT >= 26) (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
            VibrationEffect.createOneShot(20, 1)
        ) else (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(20)

    fun textAsBitmap(text: String?, textSize: Float, textColor: Int): Bitmap? {
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.LEFT
        val baseline = -paint.ascent() // ascent() is negative
        val width = (paint.measureText(text) + 0.0f).toInt() // round
        val height = (baseline + paint.descent() + 0.0f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text!!, 0f, baseline, paint)
        return image
    }

    @Suppress("DEPRECATION")
    fun showSystemUI(activity: Activity) {
        activity.showSystemUI()
    }

    @Suppress("DEPRECATION")
    fun hideSystemUI(activity: Activity) {
        activity.hideSystemUI();
    }

    fun showToast(context: Context, string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()

    }

}