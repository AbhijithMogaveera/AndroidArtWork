package com.abhijith.music.gesture

import android.view.MotionEvent
import kotlin.math.absoluteValue

class CursorMovingDirectionTracker(
    private var pixelThreshold: Int,
) {

    companion object {
        const val MOVING_UP: Int = 1
        const val NO_MOVEMENT: Int = 0
        const val MOVING_DOWN: Int = -1
    }

    private var lastEvent: MotionEvent? = null

    var state: Int = NO_MOVEMENT
        set(value) {
            if (value != NO_MOVEMENT) {
                previousState = value
            }
            field = value
        }
    var previousState: Int = NO_MOVEMENT
        private set

    fun processTouchEvent(event: MotionEvent): CursorMovingDirectionTracker {
        when (event.action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                lastEvent?.recycle()
                lastEvent = null
                state = NO_MOVEMENT
                return this
            }
        }

        if (lastEvent == null) {
            lastEvent = MotionEvent.obtain(event)
            return this
        }

        val lastEvent = this.lastEvent!!

        val deltaY = (lastEvent.y - event.y).absoluteValue

        if (deltaY > pixelThreshold) {
            state = if (lastEvent.y - event.y > 0) MOVING_UP else MOVING_DOWN
            lastEvent.recycle()
            this.lastEvent = MotionEvent.obtain(event)
        }

        return this
    }
}
