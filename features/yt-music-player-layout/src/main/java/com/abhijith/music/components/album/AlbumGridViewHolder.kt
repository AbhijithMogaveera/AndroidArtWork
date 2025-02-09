package com.abhijith.music.components.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhijith.music.databinding.SongAndAlbumViewHolderBinding
import com.abhijith.music.models.AlbumViewData
import com.abhijith.music.state.AppStateHandler

class AlbumGridViewHolder(
    val parent: ViewGroup,
    val binding: SongAndAlbumViewHolderBinding  = SongAndAlbumViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),parent, false
    )
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        albumViewData: AlbumViewData,
        appStateHandler: AppStateHandler,
    ) {
        binding.root.setOnClickListener {
            appStateHandler.requestToNavigate(albumViewData)
        }
        if(albumViewData.bannerImage is Int) {
            binding.ivBanner.setImageResource(albumViewData.bannerImage)
        }
    }
}