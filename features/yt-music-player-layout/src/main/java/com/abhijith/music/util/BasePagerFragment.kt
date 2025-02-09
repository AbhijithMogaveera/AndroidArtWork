package com.abhijith.music.util

import androidx.fragment.app.Fragment

abstract class BasePagerFragment : Fragment {

    constructor() : super()

    constructor(contentLayoutId: Int) : super(contentLayoutId)

    fun getViewPagerPosition() {

    }

    fun getPositionAtViewPager(): Int = requireArguments().getInt(KEY_POSITION)

    companion object {
        const val KEY_POSITION = "KEY_POSITION"
    }
}


