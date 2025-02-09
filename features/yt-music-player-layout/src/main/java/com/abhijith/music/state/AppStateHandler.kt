package com.abhijith.music.state

import com.abhijith.music.components.player.State
import com.abhijith.music.state.effects.NavigationEffects
import com.abhijith.music.state.effects.PanelEffects
import com.abhijith.music.state.event.NavigationEvents
import com.abhijith.music.state.event.PanelEventHandler
import com.abhijith.music.state.event.PlayerEventsHandler
import com.abhijith.music.state.event.SongAndAlbumEventsHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface AppStateHandler : PlayerEventsHandler, SongAndAlbumEventsHandler, PanelEventHandler,
    NavigationEvents {
    fun currentPlayerState(): StateFlow<State>
    val navigationEffects: Channel<NavigationEffects>
    val panelEffects: Channel<PanelEffects>
}
