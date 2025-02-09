package com.abhijith.music.components.song

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.abhijith.music.state.AppStateHandler
import com.abhijith.music.databinding.SongsViewHolderBinding
import com.abhijith.music.models.SongViewData

class SongsAdapter(
    val appStateHandler: AppStateHandler
): ListAdapter<SongViewData, SongsViewHolder>(SongsDiffCheck) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        return SongsViewHolder(
            binding = SongsViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}