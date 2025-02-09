package com.abhijith.music.util

import android.os.Bundle
import android.view.View
import com.abhijith.music.components.player.screens.TabsHostFragment

abstract class BasePagerListFragment : BasePagerFragment {
    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parentFragment = requireParentFragment()
        if (parentFragment is TabsHostFragment) {
            parentFragment.onChildFragmentViewCreated(this)
        }
    }

    abstract fun canScroll(direction: Int): Boolean

    override fun onDestroyView() {
        val parentFragment = requireParentFragment()
        if (parentFragment is TabsHostFragment) {
            parentFragment.onChildFragmentViewCreated(this)
        }
        super.onDestroyView()
    }


}
