package com.abhijith.music.components.player.screens

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.abhijith.music.components.player.PanelScrollBlocker
import com.abhijith.music.databinding.PlayerTabsBinding
import com.abhijith.music.util.BasePagerFragment
import com.google.android.material.tabs.TabLayoutMediator

/** [com.abhijith.music.components.player.screens.TabsHostFragment]*/
class PlayerTabsScreen @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), PanelScrollBlocker {

    val binding: PlayerTabsBinding = PlayerTabsBinding
        .inflate(LayoutInflater.from(context), this)

    private var canScrollEnquiry: (Int) -> Boolean = { false }

    fun setOnPageChangeListener(change: (Int) -> Unit) = apply {
        binding.vp.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    change(position)
                }
            }
        )
    }

    fun setCanScrollEnquiry(
        enquiry: (Int) -> Boolean
    ) = apply{
        canScrollEnquiry = enquiry
    }

    fun loadFragments(
        fragment: Fragment,
        callback: TabLayoutMediator.TabConfigurationStrategy
    ) = apply {
        binding.vp.adapter = TabsAdapter(fragment)
        TabLayoutMediator(binding.tl, binding.vp, callback).attach()
    }

    override fun canScroll(direction: Int): Boolean {
        return canScrollEnquiry(direction)
    }

    private class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): BasePagerFragment {
            require(position < itemCount)
            return when (position) {
                0 -> PlayerUpNextFragment()
                1 -> PlayerLyricsFragment()
                2 -> PlayerRelatedFragment()
                else -> error("Unreachable")
            }.also {
                it.arguments = bundleOf(
                    BasePagerFragment.KEY_POSITION to position
                )
            }
        }
    }
}