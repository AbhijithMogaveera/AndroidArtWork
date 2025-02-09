package com.abhijith.music.components.player

interface PanelScrollBlocker {
    fun canScroll(direction: Int): Boolean
}