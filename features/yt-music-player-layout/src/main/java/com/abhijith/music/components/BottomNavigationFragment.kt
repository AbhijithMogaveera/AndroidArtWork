package com.abhijith.music.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abhijith.music.R
import com.abhijith.music.components.player.screens.TabsHostFragment
import com.abhijith.music.databinding.HomeFragmentBinding
import com.abhijith.music.state.MusicViewModel
import com.abhijith.music.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomNavigationFragment : Fragment() {

    private val musicViewModel by activityViewModels<MusicViewModel>()

    companion object {
        private const val TAG_TABS_CONTAINER = "TAG_TABS_CONTAINER"
    }

    private val binding: HomeFragmentBinding by viewBinding(HomeFragmentBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initComponents()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                binding.parentSlidingLayout.setMusicViewModel(musicViewModel, this)
            }
        }
    }

    private fun initComponents() {
        setupAdapter()
        onHomePanelSlidingOffsetChanged()
        loadTabs()
    }

    private fun loadTabs() {
        childFragmentManager
            .beginTransaction()
            .replace(
                binding.primaryBottomSheet.binding.tabsFragmentContainer.id,
                TabsHostFragment(),
                TAG_TABS_CONTAINER
            ).commitNow()
    }

    private fun onHomePanelSlidingOffsetChanged() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                musicViewModel.currentPlayerState()
                    .map { it.primaryBSOffset }
                    .distinctUntilChanged()
                    .collectLatest {
                        (binding.bnv.layoutParams as ConstraintLayout.LayoutParams).apply {
                            bottomMargin = -(binding.bnv.measuredHeight * it).toInt()
                        }
                        binding.parentSlidingLayout.requestLayout()
                    }
            }
        }
    }

    private fun setupAdapter() {
        val suggestionsAdapter = BottomNavFragmentAdapter(childFragmentManager, this)
        val viewPager2 = binding.homeVp
        binding.bnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> viewPager2.setCurrentItem(0, false)
                R.id.page_2 -> viewPager2.setCurrentItem(1, false)
                R.id.page_3 -> viewPager2.setCurrentItem(2, false)
            }
            true
        }
        viewPager2.isUserInputEnabled = false
        viewPager2.adapter = suggestionsAdapter
    }

    private class BottomNavFragmentAdapter(
        fragmentManager: FragmentManager, lifecycleOwner: LifecycleOwner
    ) : FragmentStateAdapter(fragmentManager, lifecycleOwner.lifecycle) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            if (position == 0) return MainNavigationFragment()
            return BlankFragment()
        }
    }
}