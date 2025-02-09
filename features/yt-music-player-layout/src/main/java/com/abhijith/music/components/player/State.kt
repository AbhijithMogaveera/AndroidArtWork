package com.abhijith.music.components.player

import com.abhijith.music.models.SongViewData

enum class RepeatMode {
    NO_REPEAT, REPEAT
}

data class State(
    val songViewData: SongViewData?,
    val position: Long,
    val repeatMode: RepeatMode = RepeatMode.REPEAT,
    val primaryBSOffset: Float,
    val secondaryBSOffset: Float
)


fun State.isPrimaryPanelExpanded(): Boolean {
    return primaryBSOffset == 1f
}
fun State.isSecondaryPanelExpanded(): Boolean {
    return secondaryBSOffset == 1f
}