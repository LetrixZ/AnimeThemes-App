package com.letrix.animethemes

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.letrix.animethemes.data.DataViewModel
import com.letrix.animethemes.data.MainViewModel
import com.letrix.animethemes.models.Anime
import com.letrix.animethemes.models.Artist
import com.letrix.animethemes.models.BookmarkWrapper
import com.letrix.animethemes.models.PlaylistItem
import com.letrix.animethemes.utils.MyDebugTree
import com.letrix.animethemes.utils.SavedData
import com.letrix.animethemes.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
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

        if (savedInstanceState == null) {
            loading_layout.visibility = View.VISIBLE
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