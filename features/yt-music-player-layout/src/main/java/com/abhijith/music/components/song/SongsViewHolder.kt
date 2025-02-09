package com.abhijith.music.components.song

import androidx.recyclerview.widget.RecyclerView
import com.abhijith.music.databinding.SongsViewHolderBinding
import com.abhijith.music.models.SongViewData

class SongsViewHolder(
    val binding: SongsViewHolderBinding
):RecyclerView.ViewHolder(binding.root) {
    fun bind(data: SongViewData) {
        binding.iv.setImageResource(data.image as Int)
        binding.tvDesc.text = data.description
        binding.tvTitle.text = data.name
    }
}