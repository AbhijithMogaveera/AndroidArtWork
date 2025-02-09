package com.abhijith.music.components.player.screens

import com.abhijith.music.util.BasePagerFragment

interface TabsHost {
    fun onChildFragmentViewCreated(fragment: BasePagerFragment)
    fun onChildFragmentViewDestroyed(fragment: BasePagerFragment)
}

