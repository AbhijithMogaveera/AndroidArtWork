package com.abhijith.music.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhijith.music.components.player.PanelType
import com.abhijith.music.components.player.RepeatMode
import com.abhijith.music.components.player.State
import com.abhijith.music.components.player.isPrimaryPanelExpanded
import com.abhijith.music.components.player.isSecondaryPanelExpanded
import com.abhijith.music.data.DummyDataProvider
import com.abhijith.music.models.AlbumViewData
import com.abhijith.music.models.SongViewData
import com.abhijith.music.models.ViewData
import com.abhijith.music.state.effects.NavigationEffects
import com.abhijith.music.state.effects.PanelEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    val dummyDataProvider: DummyDataProvider
) : ViewModel(), AppStateHandler {


    override val navigationEffects: Channel<NavigationEffects> = Channel()
    override val panelEffects: Channel<PanelEffects> = Channel()
    val suggestion: MutableStateFlow<List<ViewData>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            suggestion.emit(
                dummyDataProvider
                    .getSongsSuggestions(
                        DummyDataProvider.UIFlowContext.HOME_SCREEN
                    ).first()
            )
        }
    }

    private val playerState = MutableStateFlow(
        State(
            songViewData = null,
            position = 10,
            repeatMode = RepeatMode.REPEAT,
            primaryBSOffset = 0f,
            secondaryBSOffset = 0f
        )
    )

    override fun currentPlayerState(): StateFlow<State> {
        return playerState
    }

    override fun requestPlayNextSong() {

    }

    override fun requestPlayPreviousSong() {
        viewModelScope.launch { navigationEffects.send(NavigationEffects.DisplayToastMessage("Previous Music Button Clicked")) }
    }

    override fun requestPauseCurrentSong() {
        viewModelScope.launch { navigationEffects.send(NavigationEffects.DisplayToastMessage("Pause Button Click")) }
    }

    override fun requestPlayCurrentSong() {
        viewModelScope.launch { navigationEffects.send(NavigationEffects.DisplayToastMessage("Play Button Click")) }
    }

    override fun requestSeekTo(long: Long) {
    }

    override fun requestToLike(songViewData: SongViewData) {
        viewModelScope.launch { navigationEffects.send(NavigationEffects.DisplayToastMessage("Like Button clicked")) }
    }

    override fun requestToDislike(songViewData: SongViewData) {
        viewModelScope.launch { navigationEffects.send(NavigationEffects.DisplayToastMessage("Dislike Button Clicked")) }
    }

    override fun requestToNavigate(albumViewData: AlbumViewData) {
        viewModelScope.launch {
            navigationEffects.send(
                NavigationEffects.NavigationToAlbumDetailsScreen(
                    albumViewData
                )
            )
        }
    }

    override fun requestDisplayPlayerMoreMenu() {
        viewModelScope.launch { navigationEffects.send(NavigationEffects.DisplayToastMessage("Player More Menu clicked")) }
    }

    override fun requestCollapsePrimaryPanel() {
        viewModelScope.launch { panelEffects.send(PanelEffects.CollapsePrimaryBottomSheet) }
    }

    override fun requestCollapseSecondaryPanel() {

    }

    override fun requestShuffle() {
        viewModelScope.launch { navigationEffects.send(NavigationEffects.DisplayToastMessage("Shuffling Play list")) }
    }

    override fun requestRepeat() {
        playerState.update {
            it.copy(
                repeatMode = when (it.repeatMode) {
                    RepeatMode.REPEAT -> RepeatMode.NO_REPEAT
                    RepeatMode.NO_REPEAT -> RepeatMode.REPEAT
                }
            )
        }
    }

    override fun onPanelSlided(type: PanelType, slidingOffset: Float) {
        when (type) {
            PanelType.PRIMARY_BOTTOM_SHEET -> {
                playerState.update {
                    it.copy(primaryBSOffset = slidingOffset)
                }
            }

            PanelType.SECONDARY_BOTTOM_SHEET -> {
                playerState.update {
                    it.copy(secondaryBSOffset = slidingOffset)
                }
            }
        }
    }

    override fun requestNavigateToBack() {
        viewModelScope.launch {
            val playerState = playerState.first()
            if (playerState.isPrimaryPanelExpanded()) {
                requestCollapsePrimaryPanel()
            } else if (playerState.isSecondaryPanelExpanded()) {
                requestCollapseSecondaryPanel()
            } else {
                navigationEffects.send(NavigationEffects.NavigateBack)
            }
        }
    }

}

