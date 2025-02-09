package com.abhijith.music.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.abhijith.music.R
import com.abhijith.music.components.player.PanelState
import com.abhijith.music.components.player.PanelType
import com.abhijith.music.components.player.SlidingPanelInterpolator
import com.abhijith.music.components.player.isPrimaryPanelExpanded
import com.abhijith.music.components.player.screens.MusicControllerView
import com.abhijith.music.components.player.screens.PrimaryBottomSheet
import com.abhijith.music.gesture.CursorMovingDirectionTracker
import com.abhijith.music.gesture.ViewDragHelper
import com.abhijith.music.state.MusicViewModel
import com.abhijith.music.state.effects.PanelEffects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * contains [MusicControllerView]
 * */
class MainPanelLayout
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(
    context, attrs, defStyleAttr
) {
    fun View.asMyLayoutParams() = layoutParams as MyLayoutParams
    var childPanelTopAnchorPoint: Int = top

    lateinit var childPanel: View
    lateinit var childMain: View
    private var isAnchorPointInitialized: Boolean = false

    private var childPanelBottomAnchorPoint: Int = 0


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount != 2) {
            Log.w("MainPanelLayout", "Expected 2 children, found $childCount")
        }

        childMain.layout(
            left + paddingLeft,
            top + paddingTop,
            left + paddingLeft + childMain.measuredWidth,
            top + paddingTop + childMain.measuredHeight
        )

        if (!isAnchorPointInitialized) {
            val layoutParams = childPanel.asMyLayoutParams()
            val anchorYOfPanel = layoutParams.minHeight.toInt()
            val newTop = top + childPanel.measuredHeight - anchorYOfPanel
            childPanelBottomAnchorPoint = newTop
            isAnchorPointInitialized = true
        }

        val layoutParams = childPanel.asMyLayoutParams()
        val clampedOffset = layoutParams.offset.coerceIn(0f, 1f)
        val newTopOfChildPanel = ((1 - clampedOffset) * childPanelBottomAnchorPoint).toInt()

        childPanel.layout(
            left + paddingLeft,
            newTopOfChildPanel + paddingTop,
            left + paddingLeft + childPanel.measuredWidth,
            newTopOfChildPanel + paddingTop + childPanel.measuredHeight
        )
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val childCount = childCount

        val layoutHeight = heightSize - paddingTop - paddingBottom
        val layoutWidth = widthSize - paddingLeft - paddingRight

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MyLayoutParams
            val childWidthSpec: Int = when (lp.width) {
                LayoutParams.WRAP_CONTENT -> {
                    MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST)
                }

                LayoutParams.MATCH_PARENT -> {
                    MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY)
                }

                else -> {
                    MeasureSpec.makeMeasureSpec(lp.width, widthMode)
                }
            }
            val childHeightSpec: Int = when (lp.height) {
                LayoutParams.WRAP_CONTENT -> {
                    MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.AT_MOST)
                }

                LayoutParams.MATCH_PARENT -> {
                    MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY)
                }

                else -> {
                    MeasureSpec.makeMeasureSpec(lp.height, heightMode)
                }
            }
            child.measure(childWidthSpec, childHeightSpec)

        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): MyLayoutParams {
        return MyLayoutParams(
            context, attrs
        )
    }

    override fun generateDefaultLayoutParams(): MyLayoutParams {
        return MyLayoutParams(
            100, 100
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        childPanel = requireNotNull(findViewById(R.id.primaryBottomSheet))
        childMain = requireNotNull(findViewById(R.id.homeContent))
    }

    class MyLayoutParams : MarginLayoutParams {
        var minHeight = 0f
        private var weight = 0f
        var offset = 0f

        constructor(width: Int, height: Int) : super(width, height) {}

        @SuppressLint("ResourceType")
        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val ta = c.obtainStyledAttributes(attrs, R.styleable.ParentPanel_Layout)
            minHeight = ta.getDimension(R.styleable.ParentPanel_Layout_android_minHeight, 0f)
            weight = ta.getFloat(R.styleable.ParentPanel_Layout_android_layout_weight, weight)
            offset = ta.getFloat(R.styleable.ParentPanel_Layout_android_offset, offset)
            ta.recycle()
        }
    }

    lateinit var musicViewModel: MusicViewModel
    fun setMusicViewModel(musicViewModel: MusicViewModel, scope: CoroutineScope) {
        musicControllerView.setMusicViewModel(musicViewModel, scope)
        primaryBottomSheet.setMusicViewModel(musicViewModel, scope)
        this.musicViewModel = musicViewModel
        musicViewModel.currentPlayerState().map {
                it.primaryBSOffset
            }.distinctUntilChanged().onEach {
                childPanel.asMyLayoutParams().offset = it
                childPanel.requestLayout()
            }.launchIn(scope)

        musicViewModel.panelEffects.receiveAsFlow().onEach {
                when (it) {
                    PanelEffects.CollapsePrimaryBottomSheet -> {
                        cursorMovingDirectionTrackerT5.state =
                            CursorMovingDirectionTracker.MOVING_DOWN
                        mSlideState = PanelState.COLLAPSED
                    }

                    PanelEffects.CollapseSecondaryBottomSheet -> {}
                    PanelEffects.ExpandPrimaryPanel -> {}
                }
            }.launchIn(scope)
    }

    private val cursorMovingDirectionTrackerT100 = CursorMovingDirectionTracker(100)

    private val cursorMovingDirectionTrackerT5 = CursorMovingDirectionTracker(5)


    private var disallowIntercept: Boolean = false

    private val childPanelViewDragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(
            this, 1f, SlidingPanelInterpolator, PanelDragCallBack()
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        cursorMovingDirectionTrackerT100.processTouchEvent(event)
        childPanelViewDragHelper.processTouchEvent(event)
        return true
    }

    private val musicControllerView: MusicControllerView get() = findViewById(R.id.controllerScreen)
    private val primaryBottomSheet: PrimaryBottomSheet get() = findViewById(R.id.primaryBottomSheet)

    private var disPatchEveyThingToChildMain: Boolean = false

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        val eventY = event.y
        val eventX = event.x
        val eventForControllerScreen = eventY - childPanel.y > 0
        if (eventForControllerScreen) {
            val x = MotionEvent.obtain(event).apply { setLocation(eventX, eventY - childPanel.y) }
            musicControllerView.dispatchTouchEvent(x)
            x.recycle()
        }

        when {
            event.action == MotionEvent.ACTION_DOWN && !eventForControllerScreen -> {
                disPatchEveyThingToChildMain = true
                disallowIntercept = false
            }

            event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL -> {
                childMain.dispatchTouchEvent(event)
                disPatchEveyThingToChildMain = false
            }
        }
        if (disPatchEveyThingToChildMain) {
            disallowIntercept = false
            childMain.dispatchTouchEvent(event)
            return true
        }

        onInterceptTouchEvent(event)
        return true
    }

    private var is1stDispatchToChild = true
    private var is1stDispatchToSelf = true

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        cursorMovingDirectionTrackerT5.processTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                is1stDispatchToChild = true
                dispatchEventToSelf(event)
                dispatchEventToChild(event)
                if (!disallowIntercept) return childPanelViewDragHelper.shouldInterceptTouchEvent(
                    event
                )
                return false
            }
        }

        if (!disallowIntercept) {
            val isMovingUp =
                cursorMovingDirectionTrackerT5.state == CursorMovingDirectionTracker.MOVING_UP
            if (isMovingUp && musicViewModel.currentPlayerState().value.isPrimaryPanelExpanded()) {
                if (is1stDispatchToChild) {
                    event.action = MotionEvent.ACTION_CANCEL
                    dispatchEventToSelf(event)
                    event.action = MotionEvent.ACTION_DOWN
                    is1stDispatchToChild = false
                }
                dispatchEventToChild(event)
            } else {
                if (is1stDispatchToSelf) {
                    event.action = MotionEvent.ACTION_CANCEL
                    dispatchEventToChild(event)
                    event.action = MotionEvent.ACTION_DOWN
                    is1stDispatchToSelf = false
                }
                dispatchEventToSelf(event)
            }
        } else {
            dispatchEventToChild(event)
        }
        return false
    }

    private fun dispatchEventToSelf(event: MotionEvent) {
        onTouchEvent(event)
    }

    private fun dispatchEventToChild(event: MotionEvent) {
        childPanel.dispatchTouchEvent(event)
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        this.disallowIntercept = disallowIntercept
    }


    inner class PanelDragCallBack : ViewDragHelper.Callback() {

        override fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return 1
        }

        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            when (state) {
                ViewDragHelper.STATE_IDLE -> {
                    mSlideState =
                        if (cursorMovingDirectionTrackerT5.previousState == CursorMovingDirectionTracker.MOVING_UP) {
                            PanelState.EXPANDED
                        } else {
                            PanelState.COLLAPSED
                        }
                }

                ViewDragHelper.STATE_DRAGGING -> {
                }

                ViewDragHelper.STATE_SETTLING -> {
                }
            }
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun getOrderedChildIndex(index: Int): Int {
            return indexOfChild(childPanel)
        }


        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return if (top in 0..(height - childPanel.asMyLayoutParams().minHeight).toInt()) top
            else top - dy
        }


        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int,
        ) {
            musicViewModel.onPanelSlided(
                PanelType.PRIMARY_BOTTOM_SHEET, convertScale(top).toFloat()
            )
        }
    }


    private fun convertScale(valueToConvert: Int): Double {
        val oldScaleMin = childPanelBottomAnchorPoint
        val oldScaleMax = childPanelTopAnchorPoint
        val oldScaleRange = (oldScaleMax - oldScaleMin)
        val newScaleMin = 0.0
        val newScaleMax = 1.0
        val newScaleRange = (newScaleMax - newScaleMin)
        return (((valueToConvert - oldScaleMin) * newScaleRange / oldScaleRange) + newScaleMin)
    }

    override fun computeScroll() {
        childPanelViewDragHelper.continueSettling(true)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private var mSlideState: PanelState = DEFAULT_SLIDE_STATE
        set(value) {
            when (value) {
                PanelState.EXPANDED -> {
                    slideToTopAnchorPoint()
                }

                PanelState.COLLAPSED -> {
                    slideToBottomAnchorPoint()
                }
            }
            field = value
        }

    private fun slideToBottomAnchorPoint() {
        childPanelViewDragHelper.smoothSlideViewTo(
            childPanel, 0, childPanelBottomAnchorPoint
        )
        is1stDispatchToSelf = true
    }

    private fun slideToTopAnchorPoint() {
        childPanelViewDragHelper.smoothSlideViewTo(
            childPanel, 0, childPanelTopAnchorPoint
        )
        is1stDispatchToSelf = true
    }

    companion object {
        private val DEFAULT_SLIDE_STATE = PanelState.COLLAPSED
    }
}