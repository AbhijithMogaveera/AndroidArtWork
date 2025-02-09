package com.abhijith.music.components.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.abhijith.music.state.MusicViewModel
import com.abhijith.music.databinding.AlbumDetailsFragmentBinding
import com.abhijith.music.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumDetailsFragment : Fragment() {

    val binding by viewBinding(AlbumDetailsFragmentBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val musicViewModel by activityViewModels<MusicViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBar.setNavigationOnClickListener {
            musicViewModel.requestNavigateToBack()
        }
    }
}