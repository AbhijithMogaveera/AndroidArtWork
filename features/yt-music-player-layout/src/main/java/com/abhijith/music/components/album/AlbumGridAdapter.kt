package com.abhijith.music.components.album

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.abhijith.music.models.AlbumViewData
import com.abhijith.music.state.AppStateHandler


class AlbumGridAdapter(
    private val appStateHandler: AppStateHandler
) : ListAdapter<AlbumViewData, AlbumGridViewHolder>(AlbumDiffCheck) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumGridViewHolder {
        return AlbumGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlbumGridViewHolder, position: Int) {
        holder.bind(currentList[position], appStateHandler = appStateHandler)
    }
}