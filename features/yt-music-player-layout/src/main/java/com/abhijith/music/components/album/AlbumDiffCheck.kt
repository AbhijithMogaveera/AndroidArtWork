package com.abhijith.music.components.album

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.abhijith.music.models.AlbumViewData

object AlbumDiffCheck:ItemCallback<AlbumViewData>() {
    override fun areItemsTheSame(oldItem: AlbumViewData, newItem: AlbumViewData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: AlbumViewData, newItem: AlbumViewData): Boolean {
        return oldItem == newItem
    }
}