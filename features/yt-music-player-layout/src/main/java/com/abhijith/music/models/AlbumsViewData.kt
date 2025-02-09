package com.abhijith.music.models

data class AlbumsViewData(
    val albumViewData: List<AlbumViewData>,
): ViewData {
    override fun compareTo(other: ViewData): Int {
        if(other !is AlbumsViewData){
            return -1
        }
        if (other == this){
            return 0
        }
        return 1
    }
}