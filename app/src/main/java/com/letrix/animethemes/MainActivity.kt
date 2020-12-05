package com.letrix.animethemes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.data.MainViewModel
import com.letrix.animethemes.models.*
import com.letrix.animethemes.utils.MyDebugTree
import com.letrix.animethemes.utils.SavedData
import com.letrix.animethemes.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_dialog_update.view.*
import timber.log.Timber
import timber.log.Timber.d
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataViewModel: DataViewModel

    var downloadId by Delegates.notNull<Long>()
    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id =
                intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId == id) {
                Utils.showToast(context, getString(R.string.download_completed))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Timber.plant(MyDebugTree())
        }

        registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        getData()

        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()

        if (savedInstanceState == null) {
            loading_layout.visibility = View.VISIBLE
            mainViewModel.getUpdates.observe(this) {
                if (it.isSuccessful && sharedPref.getInt("lastUpdate", 0) < it.body()!!.id) {
                    editor.putInt("lastUpdate", it.body()!!.id).apply()
                    showUpdate(it.body()!!)
                    d(sharedPref.getInt("lastUpdate", 0).toString())
                }
            }
            mainViewModel.homeList.observe(this) {
                if (it.isSuccessful) {
                    it.body()!!.bookmarks = dataViewModel.userBookmarks
                    dataViewModel.homeList = it.body()!!
                    loading_layout.visibility = View.GONE
                    val navController = findNavController(R.id.nav_host_fragment)
                    val args = Bundle()
                    args.putParcelable("homeList", it.body()!!)
                    navController.navigate(
                        R.id.homeFragment, args
                    )
                } else {
                    d("Connection error")
                }
            }
        }
    }

    fun addItem(playlistItem: PlaylistItem) {
        Utils.showToast(this, "${playlistItem.title} ${getString(R.string.added_to)}")
        Utils.vibrate(this)
        dataViewModel.userPlaylist.add(playlistItem)
    }

    fun bookmark(anime: Anime) {
        val bookmarkWrapper = BookmarkWrapper(anime)
        if (!dataViewModel.userBookmarks.contains(bookmarkWrapper)) {
            Utils.showToast(this, "${anime.title} ${getString(R.string.bookmarked)}")
            Utils.vibrate(this)
            dataViewModel.userBookmarks.add(0, bookmarkWrapper)
            dataViewModel.parentAdapter?.bookmarkAnimeAdapter?.notifyItemInserted(0)
            if (dataViewModel.userBookmarks.size == 1) dataViewModel.parentAdapter?.notifyItemChanged(
                0
            )
        } else {
            Utils.showToast(this, "${anime.title} ${getString(R.string.unbookmarked)}")
            Utils.vibrate(this)
            val pos = dataViewModel.userBookmarks.indexOf(bookmarkWrapper)
            dataViewModel.userBookmarks.remove(bookmarkWrapper)
            dataViewModel.parentAdapter?.bookmarkAnimeAdapter?.notifyItemRemoved(
                pos
            )
        }
        if (dataViewModel.userBookmarks.isEmpty()) dataViewModel.parentAdapter?.notifyItemChanged(0)
    }

    fun bookmark(artist: Artist) {
        val bookmarkWrapper = BookmarkWrapper(artist)
        if (!dataViewModel.userBookmarks.contains(bookmarkWrapper)) {
            Utils.showToast(this, "${artist.name} ${getString(R.string.bookmarked)}")
            Utils.vibrate(this)
            dataViewModel.userBookmarks.add(0, bookmarkWrapper)
            dataViewModel.parentAdapter?.bookmarkAnimeAdapter?.notifyItemInserted(0)
            if (dataViewModel.userBookmarks.size == 1) dataViewModel.parentAdapter?.notifyItemChanged(
                0
            )
        } else {
            Utils.showToast(this, "${artist.name} ${getString(R.string.unbookmarked)}")
            Utils.vibrate(this)
            val pos = dataViewModel.userBookmarks.indexOf(bookmarkWrapper)
            dataViewModel.userBookmarks.remove(bookmarkWrapper)
            dataViewModel.parentAdapter?.bookmarkAnimeAdapter?.notifyItemRemoved(
                pos
            )
        }
        if (dataViewModel.userBookmarks.isEmpty()) dataViewModel.parentAdapter?.notifyItemChanged(0)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showUpdate(updateMessage: UpdateMessage) {
        val itemView = layoutInflater.inflate(R.layout.alert_dialog_update, null)

        val builder =
            AlertDialog.Builder(this, R.style.alert_dialog)
        val updateDialog = builder.create()
        updateDialog.apply {
            setCanceledOnTouchOutside(true)
            window?.setWindowAnimations(R.style.alert_dialog)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(itemView)
            setContentView(itemView)
        }.show()

        itemView.update_title.text = updateMessage.title
        itemView.update_message.text = updateMessage.message

        itemView.update_button.text = "Update to ${updateMessage.version}"



        itemView.update_button.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/LetrixZ/animethemes-app/releases")
            )
            startActivity(browserIntent)
            updateDialog.dismiss()
        }

        itemView.cancel_button.setOnClickListener {
            updateDialog.dismiss()
        }
    }

    private fun getData() {
        dataViewModel.userPlaylist = SavedData.getPlaylist(this)
        dataViewModel.userBookmarks = SavedData.getBookmarks(this)
    }

    private fun saveData() {
        SavedData.savePlaylist(dataViewModel.userPlaylist, this)
        SavedData.saveBookmarks(dataViewModel.userBookmarks, this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && findNavController(
                R.id.nav_host_fragment
            ).currentDestination?.id == R.id.playerFragment
        ) {
            Utils.hideSystemUI(this)
        }
    }

    @Suppress("DEPRECATION")
    override fun onUserLeaveHint() {
        if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.playerFragment) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                enterPictureInPictureMode()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveData()
        unregisterReceiver(onDownloadComplete)
    }
}