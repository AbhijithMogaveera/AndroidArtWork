package com.abhijith.music.state.event

interface PlayerEventsHandler {
    fun requestPlayNextSong()
    fun requestPlayPreviousSong()
    fun requestPauseCurrentSong()
    fun requestPlayCurrentSong()
    fun requestSeekTo(long: Long)
    fun requestShuffle()
    fun requestRepeat()
}