package ru.netology.singlealbum.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.netology.singlealbum.dto.Track

class SoundsViewModel(
    private val mediaPlayer: MediaPlayer,

    ) : ViewModel() {

    val isPlaying
        get() = flow {
            while (true) {
                emit(mediaPlayer.isPlaying)
                delay(15)
            }
        }

    val isPlayingDuration
        get() = flow {
            while (true) {
                emit(mediaPlayer.duration)
                delay(15)
            }
        }

    val isPlayingCurrentPosition
        get() = flow {
            while (true) {
                emit(mediaPlayer.currentPosition)
                delay(15)
            }
        }

    val dataAlbumTrack
        get() = flow {
            while (true) {
                emit(_dataAlbumTracks)
                delay(15)
            }
        }
    private var _dataAlbumTracks: List<Track> = listOf()
    private var currentTrack: Track? = null

    fun setAlbumTracks(value: List<Track>) {
        _dataAlbumTracks = value
    }

    fun onPlayItem(track: Track) {
        mediaPlayer.pause()
        _dataAlbumTracks = _dataAlbumTracks.map {
            it.copy(isPlaying = false)
        }
        if (currentTrack?.id != track.id) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/${track.file}")
            mediaPlayer.prepare()
            _dataAlbumTracks = _dataAlbumTracks.map {
                if (it.id == track.id) {
                    currentTrack = it
                    val minuteDur = mediaPlayer.duration / 60_000
                    val secondsDur = (mediaPlayer.duration / 1_000) - (minuteDur * 60)
                    it.copy(
                        isPlaying = true,
                        duration = "$minuteDur:${if (secondsDur < 10) "0" else ""}$secondsDur"
                    )
                } else {
                    it
                }
            }
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                val thisTrackInData = _dataAlbumTracks.find {
                    it.id == track.id
                }
                val thisTrackIndex = _dataAlbumTracks.indexOf(thisTrackInData)
                val lastIndex = _dataAlbumTracks.lastIndex
                if (lastIndex == thisTrackIndex) {
                    onPlayItem(_dataAlbumTracks[0])
                } else {
                    onPlayItem(_dataAlbumTracks[thisTrackIndex + 1])
                }
            }
        }
    }
}