package com.abhijith.music.state.event

import com.abhijith.music.components.player.PanelType

interface PanelEventHandler {
    fun onPanelSlided(
        type: PanelType,
        slidingOffset: Float
    )
    fun requestCollapsePrimaryPanel()
    fun requestCollapseSecondaryPanel()
}