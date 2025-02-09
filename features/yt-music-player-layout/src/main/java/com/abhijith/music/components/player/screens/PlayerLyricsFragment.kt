package com.abhijith.music.components.player.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.abhijith.music.R
import com.abhijith.music.databinding.PlayerLyricsFragmentBinding
import com.abhijith.music.util.BasePagerListFragment
import com.abhijith.music.util.viewBinding

class PlayerLyricsFragment : BasePagerListFragment() {

    val binding by viewBinding(PlayerLyricsFragmentBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun canScroll(direction: Int): Boolean {
        return view?.findViewById<RecyclerView>(R.id.rv)?.canScrollVertically(direction)
            ?: view?.findViewById<NestedScrollView>(R.id.nsv)?.canScrollVertically(direction)
            ?: false
    }
}
