package com.abhijith.music.models

data class SongsViewData (
    val songViewData: List<SongViewData>
):ViewData {
    override fun compareTo(other: ViewData): Int {
        if(other !is SongsViewData){
            return -1
        }
        if(other == this){
            return 0
        }
        return 1
    }
}