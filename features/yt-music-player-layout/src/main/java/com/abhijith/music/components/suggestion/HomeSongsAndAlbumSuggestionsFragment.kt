package com.abhijith.music.components.suggestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abhijith.music.state.MusicViewModel
import com.abhijith.music.components.GeneralRecyclerAdapter
import com.abhijith.music.databinding.MusicSuggestionFragmentBinding
import com.abhijith.music.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeSongsAndAlbumSuggestionsFragment : Fragment() {

    private val binding by viewBinding(MusicSuggestionFragmentBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    private val musicViewModel by activityViewModels<MusicViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val generalRecyclerAdapter = GeneralRecyclerAdapter(
            playerActions = musicViewModel
        )
        binding.homeScreenRv.adapter = generalRecyclerAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
               musicViewModel
                   .suggestion
                   .collectLatest(generalRecyclerAdapter::submitList)
            }
        }
    }
}