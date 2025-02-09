package com.abhijith.music.components

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhijith.music.models.AlbumsViewData
import com.abhijith.music.models.HeaderViewHolder
import com.abhijith.music.models.HeaderViewData
import com.abhijith.music.models.ViewData
import com.abhijith.music.state.AppStateHandler
import com.abhijith.music.components.suggestion.SuggestionsDiffCheck
import com.abhijith.music.models.SongsViewData

class GeneralRecyclerAdapter(
    val playerActions: AppStateHandler
) : ListAdapter<ViewData, RecyclerView.ViewHolder>(
    SuggestionsDiffCheck
) {

    companion object {
        private const val ViewTypeHeaderViewHolder = 0
        private const val ViewTypeAlbumViewHolder = 1
        private const val ViewTypeSongsViewHolder = 2
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewTypeHeaderViewHolder -> HeaderViewHolder(parent)
            ViewTypeAlbumViewHolder, ViewTypeSongsViewHolder -> NestedRecyclerviewHolder(parent, playerActions)
            else -> error("Unreachable")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is HeaderViewData -> ViewTypeHeaderViewHolder
            is AlbumsViewData -> ViewTypeAlbumViewHolder
            is SongsViewData -> ViewTypeSongsViewHolder
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (val data = currentList[position]) {
            is HeaderViewData -> (holder as HeaderViewHolder).bind(data)
            is AlbumsViewData -> (holder as NestedRecyclerviewHolder).bind(data)
            is SongsViewData -> (holder as NestedRecyclerviewHolder).bind(data)
        }
    }

}