package com.abhijith.music.components.song

import androidx.recyclerview.widget.DiffUtil
import com.abhijith.music.models.SongViewData

object SongsDiffCheck : DiffUtil.ItemCallback<SongViewData>() {
    override fun areItemsTheSame(
        oldItem: SongViewData,
        newItem: SongViewData
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: SongViewData,
        newItem: SongViewData
    ): Boolean {
        return oldItem == newItem
    }

}