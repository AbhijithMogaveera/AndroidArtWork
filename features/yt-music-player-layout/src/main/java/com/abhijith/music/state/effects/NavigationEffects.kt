package com.abhijith.music.state.effects

import com.abhijith.music.models.AlbumViewData

sealed class NavigationEffects {
    data class NavigationToAlbumDetailsScreen(
        val albumViewData: AlbumViewData
    ) : NavigationEffects()
    data class DisplayToastMessage(val message: String) : NavigationEffects()
    data object NavigateBack : NavigationEffects()
}