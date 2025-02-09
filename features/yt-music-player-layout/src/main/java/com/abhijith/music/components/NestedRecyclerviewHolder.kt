package com.abhijith.music.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhijith.music.databinding.RecyclerViewViewHolderBinding
import com.abhijith.music.models.AlbumsViewData
import com.abhijith.music.components.album.AlbumGridAdapter
import com.abhijith.music.state.AppStateHandler
import com.abhijith.music.components.song.SongsAdapter
import com.abhijith.music.models.SongsViewData

class NestedRecyclerviewHolder(
    parent: ViewGroup,
    appStateHandler: AppStateHandler,
    val binding: RecyclerViewViewHolderBinding = RecyclerViewViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) : RecyclerView.ViewHolder(binding.root) {

    private val albumGridAdapter: AlbumGridAdapter by lazy { AlbumGridAdapter(appStateHandler) }
    private val songListAdapter: SongsAdapter by lazy { SongsAdapter(appStateHandler) }

    init {
        binding.root.layoutManager = LinearLayoutManager(binding.root.context)
    }

    fun bind(albumViewHolderData: AlbumsViewData) {
        binding.root.layoutManager = GridLayoutManager(binding.root.context, 3)
        binding.root.adapter = albumGridAdapter
        albumGridAdapter.submitList(albumViewHolderData.albumViewData)
    }

    fun bind(songsViewData: SongsViewData) {
        binding.root.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.root.adapter = songListAdapter
        songListAdapter.submitList(songsViewData.songViewData)
    }
}
