package ru.netology.singlealbum.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import okhttp3.*
import ru.netology.singlealbum.R
import ru.netology.singlealbum.adapters.OnInteractionListener
import ru.netology.singlealbum.adapters.TrackAdapter
import ru.netology.singlealbum.dto.Album
import ru.netology.singlealbum.dto.Track
import ru.netology.singlealbum.viewmodel.SoundsViewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var listItem: RecyclerView
    private lateinit var adapter: TrackAdapter
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val viewModel: SoundsViewModel = SoundsViewModel(mediaPlayer)
    private val interactionListener = object : OnInteractionListener {
        override fun onTap(track: Track) {
            viewModel.onPlayItem(track)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listItem = findViewById(R.id.list_item)
        adapter = TrackAdapter(interactionListener)
        listItem.adapter = adapter
        requestData()
        flowData()
        listeners()
    }

    private fun flowData() {
        lifecycleScope.launchWhenCreated {
            viewModel.isPlaying.collectLatest {
                if (mediaPlayer.isPlaying) {
                    findViewById<ImageButton>(R.id.play_album).setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                } else {
                    findViewById<ImageButton>(R.id.play_album).setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.dataAlbumTrack.collectLatest {
                adapter.submitList(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.isPlayingCurrentPosition.collectLatest {
                if (it in 1..9999999) {
                    val minuteDur = it / 60_000
                    val secondsDur = (it / 1_000) - (minuteDur * 60)
                    val currentPosition =
                        "$minuteDur:${if (secondsDur < 10) "0" else ""}$secondsDur"
                    findViewById<TextView>(R.id.sound_current_position).text = currentPosition
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.isPlayingDuration.collectLatest {
                if (it in 1..9999999) {
                    val minuteDur = it / 60_000
                    val secondsDur = (it / 1_000) - (minuteDur * 60)
                    val duration = "/ $minuteDur:${if (secondsDur < 10) "0" else ""}$secondsDur"
                    findViewById<TextView>(R.id.sound_duration).text = duration
                }
            }
        }
    }

    private fun listeners() {
        findViewById<ImageButton>(R.id.play_album).setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }
    }

    private fun requestData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/album.json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("MainActivity", "Error fetching JSON")
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body?.string()
                val album = Gson().fromJson(jsonString, Album::class.java)
                val albumTracks = album.tracks
                viewModel.setAlbumTracks(albumTracks)
                runOnUiThread {
                    findViewById<TextView>(R.id.album_name).text = album.title
                    findViewById<TextView>(R.id.artist_name).text = album.artist
                    findViewById<TextView>(R.id.genre_name).text = album.genre
                    findViewById<TextView>(R.id.year_name).text = album.published
                }
            }
        })
    }
}