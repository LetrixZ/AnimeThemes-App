package com.letrix.animethemes.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.letrix.animethemes.MainActivity
import com.letrix.animethemes.R
import com.letrix.animethemes.adapters.player.PlaylistAdapter
import com.letrix.animethemes.data.MainViewModel
import com.letrix.animethemes.kotlin.interfaces.PlayerListener
import com.letrix.animethemes.kotlin.utils.DescriptionAdapter
import com.letrix.animethemes.models.Playlist
import com.letrix.animethemes.models.PlaylistItem
import com.letrix.animethemes.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.player_controller.*
import timber.log.Timber

class PlayerFragment : Fragment(), PlayerListener {

    companion object {
        fun newInstance(playlist: Playlist): PlayerFragment {
            val args = Bundle()
            args.putParcelable("playlist", playlist);
            val fragment = PlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var playlist: Playlist

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
        playlist = arguments?.getParcelable("playlist")!!
//        val inflater = TransitionInflater.from(requireContext())
//        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        // This callback will only be called when MyFragment is at least Started.

        // This callback will only be called when MyFragment is at least Started.
        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        requireActivity().requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aspect_ratio_layout.setAspectRatio(16f / 9f)
        initPlayer()
        initPlaylist()

        fullscreen_button.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) requireActivity().requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else requireActivity().requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) player_theme_title.visibility =
            View.GONE else player_theme_title.visibility = View.VISIBLE

        updateInfo(playlist.last)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            player_theme_title.visibility = View.VISIBLE
            aspect_ratio_layout.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            activity?.let { Utils.hideSystemUI(it) }
        } else {
            player_theme_title.visibility = View.GONE
            aspect_ratio_layout.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
            activity?.let { Utils.showSystemUI(it) }
        }
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(requireActivity()).build()
        player.setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MOVIE)
                .build(), true
        )
        player_view.player = player
//        player.prepare(BuildMediaSource.buildConcatenatingMediaSource(activity as MainActivity, playlist))
        val mediaItems: ArrayList<MediaItem> = ArrayList()
        for (item in playlist.items) {
            mediaItems.add(MediaItem.fromUri(Uri.parse(item.mirror.mirror)))
        }
        player.setMediaItems(mediaItems)
        player.prepare()

        player.seekTo(playlist.last, C.TIME_UNSET)
        player.playWhenReady = true
        player_view.keepScreenOn = true

        initMediaSession()
        initNotificationManager()

        player.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(
                    requireActivity(),
                    "Player error, please restart the player",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
                super.onTracksChanged(trackGroups, trackSelections)
                updateInfo(player.currentWindowIndex)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> progress_bar.visibility = View.VISIBLE
                    Player.STATE_READY -> progress_bar.visibility = View.GONE
                    Player.STATE_ENDED -> player_view.keepScreenOn = true
                    Player.STATE_IDLE -> player_view.keepScreenOn = true
                }
            }
        })
    }

    private fun initPlaylist() {
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            playlistAdapter = PlaylistAdapter(playlist, this@PlayerFragment)
            adapter = playlistAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateInfo(position: Int) {
        val item = playlist[position]
        val text = SpannableString("${item.title} | ${item.name} (${item.type})")
        text.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            item.title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        player_theme_title.setText(text, TextView.BufferType.SPANNABLE)
        theme_type_quality.text = "${item.type.let { Utils.parseType(it) }} ${
            item.mirror.quality.let {
                Utils.parseQuality(
                    it,
                    requireActivity(),
                    1
                )
            }
        }"
        theme_title.text = item.title
        anime_title.text = item.name
        Picasso.get().load(item.cover).into(anime_cover)
        playlist.last = position
        playlistAdapter.updatePos(position)
        theme_views.text = "${item.views} " +
                if (item.views == 1) getString(R.string.view)
                else getString(R.string.views)
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(requireActivity(), "ExoPlayer").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            isActive = true
        }
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            val queueNavigator = object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(
                    player: Player,
                    windowIndex: Int
                ): MediaDescriptionCompat {
                    val item: PlaylistItem = playlist[player.currentWindowIndex]
                    return MediaDescriptionCompat.Builder().apply {
                        setTitle("${item.title} | ${item.name}")
                        setDescription("${item.name} (${item.type})")
                    }.build()
                }
            }
            setQueueNavigator(queueNavigator)
            setPlayer(player)
        }
    }

    private fun initNotificationManager() {
        playerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(
                requireActivity(),
                "Channel ID",
                R.string.app_name,
                R.string.app_name,
                R.string.app_name,
                DescriptionAdapter(playlist, requireActivity()),
                object : PlayerNotificationManager.NotificationListener {
                    override fun onNotificationCancelled(
                        notificationId: Int,
                        dismissedByUser: Boolean
                    ) {
                        if (dismissedByUser && isAdded) {
                            releasePlayer()
                        }
                    }
                }).apply {
                setUseNavigationActions(true)
                setUsePlayPauseActions(true)
                setFastForwardIncrementMs(10000)
                setRewindIncrementMs(10000)
                setUseStopAction(false)
                setUseChronometer(true)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setPriority(NotificationCompat.PRIORITY_LOW)
                setMediaSessionToken(mediaSession.sessionToken)
            }
    }

    override fun onResume() {
        super.onResume()
        playerNotificationManager.setPlayer(null)
    }

    override fun onPause() {
        super.onPause()
        playerNotificationManager.setPlayer(player)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        activity?.let { Utils.showSystemUI(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        activity?.let { Utils.showSystemUI(it) }
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
    }

    override fun onThemeClick(position: Int) {
        player.seekTo(position, C.TIME_UNSET)
        player.playWhenReady = true
        recycler_view.smoothScrollToPosition(0)
    }

    private fun releasePlayer() {
        player.release()
        mediaSession.release()
        mediaSessionConnector.setPlayer(null)
        playerNotificationManager.setPlayer(null)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            player_view.hideController()
            player_view.useController = false
        } else {
            player_view.useController = true
            player_view.showController()
        }
    }

}