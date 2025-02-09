package com.abhijith.music.models

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhijith.music.databinding.HeaderViewHolderBinding


class HeaderViewHolder(
    parent: ViewGroup,
    private val binding: HeaderViewHolderBinding = HeaderViewHolderBinding.inflate(
        LayoutInflater.from(
            parent.context
        ), parent, false
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        data: HeaderViewData
    ) {
        binding.tvTitle.text = data.title
        binding.tvDesc.text = data.subtitle
        if (data.iconLeft is Int) {
            binding.iconLeft.setImageResource(data.iconLeft)
        }
    }
}