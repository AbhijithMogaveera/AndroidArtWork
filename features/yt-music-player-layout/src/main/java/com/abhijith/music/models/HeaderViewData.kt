package com.abhijith.music.models

data class HeaderViewData(
    val title: String,
    val subtitle: String?,
    val iconRight: Any?,
    val iconLeft: Any?,
) : ViewData {
    override fun compareTo(other: ViewData): Int {
        if (other !is HeaderViewData) {
            return -1
        }
        if (other == this) {
            return 0
        }
        return 1
    }
}