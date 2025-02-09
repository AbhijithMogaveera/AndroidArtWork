package com.abhijith.music.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abhijith.music.R
import com.abhijith.music.databinding.MainNavigationFragmentBinding
import com.abhijith.music.state.MusicViewModel
import com.abhijith.music.state.effects.NavigationEffects
import com.abhijith.music.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainNavigationFragment : Fragment() {

    private val musicViewModel by activityViewModels<MusicViewModel>()

    private val options = NavOptions
        .Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()

    private val binding by viewBinding(MainNavigationFragmentBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicViewModel.navigationEffects.receiveAsFlow().collectLatest {
                    when (it) {
                        is NavigationEffects.NavigationToAlbumDetailsScreen -> navigateToAlbum(it)
                        is NavigationEffects.NavigateBack -> navigateBack()
                        is NavigationEffects.DisplayToastMessage -> toast(it)
                    }
                }
            }
        }
    }

    private fun toast(it: NavigationEffects.DisplayToastMessage) {
        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun navigateToAlbum(it: NavigationEffects.NavigationToAlbumDetailsScreen) {
        findNavController().navigate(
            R.id.fragmentPlayList,
            bundleOf("id" to it.albumViewData.id),
            options,
        )
    }

}