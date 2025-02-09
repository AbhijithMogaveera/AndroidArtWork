package com.abhijith.music.components.player.screens

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
import com.abhijith.music.databinding.FragmentPlayerHostBinding
import com.abhijith.music.util.BasePagerFragment
import com.abhijith.music.util.BasePagerListFragment
import com.abhijith.music.util.viewBinding
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TabsHostFragment : Fragment(), TabsHost, TabLayoutMediator.TabConfigurationStrategy {

    private val binding: FragmentPlayerHostBinding by viewBinding(
        FragmentPlayerHostBinding::inflate
    )

    private var fragments: MutableList<BasePagerFragment> = mutableListOf()

    private var currentlyVisibleFragment: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val musicViewModel by activityViewModels<MusicViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.loadFragments(fragment = this, callback = this)
            .setOnPageChangeListener { currentlyVisibleFragment = it }
            .setCanScrollEnquiry(::canScroll)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                musicViewModel.currentPlayerState().collectLatest {
                    onSlidingOffsetChanged(it.secondaryBSOffset)
                }
            }
        }
    }

    private fun canScroll(direction: Int): Boolean {
        return fragments.first {
            it is BasePagerListFragment && it.getPositionAtViewPager() == currentlyVisibleFragment
        }.let {
            (it as BasePagerListFragment).canScroll(direction)
        }
    }

    override fun onChildFragmentViewCreated(fragment: BasePagerFragment) {
        fragments.add(fragment)
    }

    override fun onChildFragmentViewDestroyed(fragment: BasePagerFragment) {
        fragments.remove(fragment)
    }

    override fun onConfigureTab(tab: Tab, position: Int) {
        tab.text = when (position) {
            0 -> "UP NEXT"
            1 -> "LYRICS"
            2 -> "RELATED"
            else -> error("Unreachable")
        }
    }

    private fun onSlidingOffsetChanged(
        offset: Float,
    ) {
        binding.root.binding.vp.alpha = offset
    }

}
