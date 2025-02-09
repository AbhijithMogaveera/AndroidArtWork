package com.abhijith.music.components.player.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.abhijith.music.state.MusicViewModel
import com.abhijith.music.R
import com.abhijith.music.components.GeneralRecyclerAdapter
import com.abhijith.music.databinding.PlayerUpNextFragmentBinding
import com.abhijith.music.util.BasePagerListFragment
import com.abhijith.music.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class PlayerUpNextFragment : BasePagerListFragment(R.layout.player_up_next_fragment) {

    private val musicViewModel by activityViewModels<MusicViewModel>()

    val binding: PlayerUpNextFragmentBinding by viewBinding(PlayerUpNextFragmentBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val generalRecyclerAdapter = GeneralRecyclerAdapter(playerActions = musicViewModel)
        binding.rv.adapter = generalRecyclerAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                musicViewModel
                    .suggestion
                    .collectLatest(generalRecyclerAdapter::submitList)
            }
        }
    }

    override fun canScroll(direction: Int): Boolean {
        return view?.findViewById<RecyclerView>(R.id.rv)?.canScrollVertically(direction)
            ?: view?.findViewById<NestedScrollView>(R.id.nsv)?.canScrollVertically(direction)
            ?: false
    }
}
