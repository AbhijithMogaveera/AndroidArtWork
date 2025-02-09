package com.abhijith.music.components.player

import android.view.ViewGroup

fun <T> ViewGroup.findFirstOccurrence(clazz: Class<T>): T? {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (clazz.isInstance(child)) {
            return clazz.cast(child)
        }
        if (child is ViewGroup) {
            val result = child.findFirstOccurrence(clazz)
            if (result != null) {
                return result
            }
        }
    }
    return null
}

