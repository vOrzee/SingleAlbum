package ru.netology.singlealbum.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.singlealbum.R
import ru.netology.singlealbum.databinding.ItemSoundBinding
import ru.netology.singlealbum.dto.Track

interface OnInteractionListener {
    fun onTap(track: Track) {}
}

class TrackAdapter(
    private val interactionListener: OnInteractionListener
) : ListAdapter<Track, TrackViewHolder>(PointsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            ItemSoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.renderingPostStructure(track)
    }
}

class PointsDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}

class TrackViewHolder(
    private val binding: ItemSoundBinding,
    private val interactionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun renderingPostStructure(track: Track) {
        with(binding) {
            trackName.text = track.file
            playItem.setImageResource(
                if (track.isPlaying) {
                    R.drawable.ic_baseline_pause_circle_filled_24
                } else {
                    R.drawable.ic_baseline_play_circle_filled_24
                }
            )
            trackTime.text = track.duration ?: ""
            soundListeners(track)
        }
    }

    private fun soundListeners(track: Track) {
        with(binding) {
            playItem.setOnClickListener {
                interactionListener.onTap(track)
            }
        }
    }
}