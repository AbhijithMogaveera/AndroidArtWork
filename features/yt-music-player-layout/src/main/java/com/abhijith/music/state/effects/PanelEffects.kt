package com.abhijith.music.state.effects

sealed class PanelEffects {
    data object ExpandPrimaryPanel : PanelEffects()
    data object CollapsePrimaryBottomSheet : PanelEffects()
    data object CollapseSecondaryBottomSheet : PanelEffects()
}