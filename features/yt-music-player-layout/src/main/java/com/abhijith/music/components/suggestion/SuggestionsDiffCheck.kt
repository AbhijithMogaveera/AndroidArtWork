package com.abhijith.music.components.suggestion

import androidx.recyclerview.widget.DiffUtil
import com.abhijith.music.models.ViewData

object SuggestionsDiffCheck : DiffUtil.ItemCallback<ViewData>() {
    override fun areItemsTheSame(
        oldItem: ViewData,
        newItem: ViewData
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: ViewData,
        newItem: ViewData
    ): Boolean {
        return oldItem == newItem
    }

}