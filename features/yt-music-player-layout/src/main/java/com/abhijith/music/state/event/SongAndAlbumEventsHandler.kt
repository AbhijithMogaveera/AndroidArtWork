package com.abhijith.music.state.event

import com.abhijith.music.models.AlbumViewData
import com.abhijith.music.models.SongViewData

interface SongAndAlbumEventsHandler {
    fun requestToLike(songViewData: SongViewData)
    fun requestToDislike(songViewData: SongViewData)
    fun requestToNavigate(albumViewData: AlbumViewData)
}