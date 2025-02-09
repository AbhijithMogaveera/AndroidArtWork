package com.abhijith.music.components.player.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.Dimension
import androidx.annotation.Dimension.Companion.DP
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.abhijith.music.R
import com.abhijith.music.components.player.PanelScrollBlocker
import com.abhijith.music.components.player.PanelState
import com.abhijith.music.components.player.PanelType
import com.abhijith.music.components.player.SlidingPanelInterpolator
import com.abhijith.music.components.player.findFirstOccurrence
import com.abhijith.music.components.player.isPrimaryPanelExpanded
import com.abhijith.music.databinding.PrimaryBottomSheetLayoutBinding
import com.abhijith.music.gesture.CursorMovingDirectionTracker
import com.abhijith.music.gesture.ViewDragHelper
import com.abhijith.music.state.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.math.max
import kotlin.math.roundToInt

class PrimaryBottomSheet
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(
    context,
    attrs
) {

    val binding = PrimaryBottomSheetLayoutBinding.inflate(LayoutInflater.from(context), this)

    //Android Side Effects
    private var insets: WindowInsets? = null

    //Song banner transition
    private val transitionStartView: View = binding.controllerScreen.binding.bannerPlaceHolderCollapse
    private val transitionEndView: View = binding.controllerScreen.binding.bannerPlaceHolderExpand
    private val transitionView: View = binding.transitionView

    //Secondary bottom sheet
    private val secondaryBottomSheet: View = binding.secondaryBottomSheet
    private var secondaryBottomSheetCurrentY: Int = -1
    private val secondaryBottomSheetBottomYLimit: Int get() = (height - tabPlaceHolderHeight() - getBottomInsets()).roundToInt()
    private val secondaryBottomSheetTopYLimit: Int get() = (tabPlaceHolderHeight() + getTopInsets()).roundToInt()

    private var initialY: Float = 0f

    private val fmd100 = CursorMovingDirectionTracker(100)

    private var isGrandChildPanelIsDragging: Boolean = false

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        this.insets = insets
        return super.onApplyWindowInsets(insets)
    }


    override fun computeScroll() {
        grandChileGrandChildPanelDragHelper.continueSettling(true)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private var _slideState: PanelState = PanelState.COLLAPSED

    lateinit var musicViewModel: MusicViewModel

    fun setMusicViewModel(musicViewModel: MusicViewModel, scope: CoroutineScope) {
        this.musicViewModel = musicViewModel
        val appState = musicViewModel.currentPlayerState()
        listOf(
            appState.map { it.primaryBSOffset to PanelType.PRIMARY_BOTTOM_SHEET }
                .distinctUntilChanged(),
            appState.map { it.secondaryBSOffset to PanelType.SECONDARY_BOTTOM_SHEET }
                .distinctUntilChanged()
        ).map {
            it.onEach { (slidingOffset, panelType) ->
                onSlidingOffsetChanged(panelType, slidingOffset)
            }
        }.let { flowList ->
            combine(flowList) {
            }.onStart {
                doOnLayout {
                    onSlidingOffsetChanged(PanelType.PRIMARY_BOTTOM_SHEET, 0f)
                }
            }.launchIn(scope)
        }
    }

    fun getSlideState() = _slideState

    fun setSlideState(value: PanelState) {
        when (value) {
            PanelState.EXPANDED ->
                grandChileGrandChildPanelDragHelper
                    .smoothSlideViewTo(secondaryBottomSheet, 0, secondaryBottomSheetTopYLimit)

            PanelState.COLLAPSED ->
                grandChileGrandChildPanelDragHelper
                    .smoothSlideViewTo(secondaryBottomSheet, 0, secondaryBottomSheetBottomYLimit)

        }
        _slideState = value
    }

    private var viewPanelCurrentDY: Int = 0

    private val grandChileGrandChildPanelDragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(
            this,
            0.5f,
            SlidingPanelInterpolator,
            GrandChildPanelDragHelper()
        )
    }


    inner class GrandChildPanelDragHelper : ViewDragHelper.Callback() {

        override fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return 1
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return musicViewModel.currentPlayerState().value.isPrimaryPanelExpanded()
        }

        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            when (state) {
                ViewDragHelper.STATE_IDLE -> {
                    isGrandChildPanelIsDragging = false
                    if (fmd100.previousState == CursorMovingDirectionTracker.MOVING_UP) {
                        setSlideState(PanelState.EXPANDED)
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        setSlideState(PanelState.COLLAPSED)
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }

                ViewDragHelper.STATE_DRAGGING -> {
                    parent.requestDisallowInterceptTouchEvent(true)
                    isGrandChildPanelIsDragging = true
                }

                ViewDragHelper.STATE_SETTLING -> {
                    parent.requestDisallowInterceptTouchEvent(true)
                    isGrandChildPanelIsDragging = true
                }
            }
        }

        override fun getOrderedChildIndex(index: Int): Int {
            return indexOfChild(secondaryBottomSheet)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return if (top in transitionStartView.bottom..secondaryBottomSheetBottomYLimit) top else top - dy
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int,
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            secondaryBottomSheetCurrentY = top
            viewPanelCurrentDY = dy
            val offset = convertScale(secondaryBottomSheetCurrentY)
            musicViewModel.onPanelSlided(
                PanelType.SECONDARY_BOTTOM_SHEET,
                offset
            )
        }
    }

    private var canScrollRv: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
            requestDisallowInterceptTouchEvent(false)
        }
        try {
            grandChileGrandChildPanelDragHelper.processTouchEvent(event)
        } catch (_: Exception) {

        }
        fmd100.processTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        val isPanelExpanded = getSlideState() == PanelState.EXPANDED

        if (!isPanelExpanded) {
            return true
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialY = ev.y
                grandChileGrandChildPanelDragHelper.processTouchEvent(MotionEvent.obtain(ev))
                return false
            }
        }

        val panelScrollBlocker = findFirstOccurrence(PanelScrollBlocker::class.java)
        canScrollRv = panelScrollBlocker?.canScroll(-1) ?: true
        if (canScrollRv) {
            return false
        }
        return (ev.y - initialY > 0)
    }

    private fun convertScale(oldValueToConvert: Int): Float {
        val oldScaleMin = secondaryBottomSheetBottomYLimit
        val oldScaleMax = secondaryBottomSheetTopYLimit
        val oldScaleRange = (oldScaleMax - oldScaleMin)
        val newScaleMin = 0.0f
        val newScaleMax = 1.0f
        val newScaleRange = (newScaleMax - newScaleMin)
        return ((oldValueToConvert - oldScaleMin) * newScaleRange / oldScaleRange) + newScaleMin
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val parentWidth = right - left
        val parentHeight = bottom - top

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child.visibility == GONE)
                continue

            val lp = child.layoutParams as LayoutParams
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            var childLeft = lp.leftMargin
            var childTop = lp.topMargin

            when (lp.gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.CENTER_HORIZONTAL -> childLeft = (parentWidth - childWidth) / 2
                Gravity.RIGHT -> childLeft = parentWidth - childWidth - lp.rightMargin
                Gravity.LEFT, 0 -> {}
            }

            when (lp.gravity and Gravity.VERTICAL_GRAVITY_MASK) {
                Gravity.CENTER_VERTICAL -> childTop = (parentHeight - childHeight) / 2
                Gravity.BOTTOM -> childTop =
                    parentHeight - childHeight - dpToPx(context, 70).roundToInt() - lp.bottomMargin

                Gravity.TOP, 0 -> {}
            }

            val childRight = childLeft + childWidth
            val childBottom = childTop + childHeight

            if (child === secondaryBottomSheet) {
                if (!isGrandChildPanelIsDragging) {
                    secondaryBottomSheetCurrentY = max(
                        a = (parentHeight - tabPlaceHolderHeight() - getBottomInsets()).roundToInt(),
                        b = secondaryBottomSheetBottomYLimit
                    )
                }
                secondaryBottomSheet.layout(
                    childLeft,
                    childTop + secondaryBottomSheetCurrentY,
                    childRight,
                    childBottom + secondaryBottomSheetCurrentY
                )
                continue
            }
            child.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return PrimaryBottomSheetLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return PrimaryBottomSheetLayoutParams(lp ?: return generateDefaultLayoutParams())
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return PrimaryBottomSheetLayoutParams(context, attrs ?: return generateDefaultLayoutParams())
    }


    private fun onSlidingOffsetChanged(
        panelType: PanelType,
        slidingOffset: Float
    ) {
        val slidingOffset = when (panelType) {
            PanelType.PRIMARY_BOTTOM_SHEET -> slidingOffset
            PanelType.SECONDARY_BOTTOM_SHEET -> 1f - slidingOffset
        }
        val minWidth: Float = transitionStartView.width.toFloat()
        val maxWidth: Float = transitionEndView.width.toFloat()
        val size: Float = slidingOffset * (maxWidth)

        val sheetLayoutParams: PrimaryBottomSheetLayoutParams =
            transitionView.layoutParams as PrimaryBottomSheetLayoutParams
        if (size in minWidth..maxWidth) {
            transitionView.layoutParams =
                sheetLayoutParams.copy(size.toInt(), size.toInt())
        } else {
            transitionView.layoutParams =
                sheetLayoutParams.copy(minWidth.toInt(), minWidth.toInt())
        }

        val oppSlidingOffset = 1 - slidingOffset
        val startMargin = oppSlidingOffset * transitionView.marginStart
        val topMargin = oppSlidingOffset * transitionView.marginTop

        transitionView.x = startMargin + (slidingOffset * transitionEndView.x)
        val fl1 = if (panelType == PanelType.PRIMARY_BOTTOM_SHEET) slidingOffset else 1f
        transitionView.y =
            topMargin + (slidingOffset * transitionEndView.y) + ((insets?.systemWindowInsetTop
                ?: 0) * fl1)
    }


    private fun dpToPx(context: Context, @Dimension(unit = DP) dp: Int): Float {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxWidth = 0
        var maxHeight = 0
        var childState = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) continue

            val lp = child.layoutParams as MarginLayoutParams
            if (child === binding.secondaryBottomSheet) {
                val heightSpec =
                    MeasureSpec.getSize(heightMeasureSpec) - context.resources.getDimension(R.dimen.tabLayoutHeight)
                        .roundToInt()
                val adjustedHeightSpec =
                    MeasureSpec.makeMeasureSpec(heightSpec, MeasureSpec.EXACTLY)

                measureChildWithMargins(
                    child,
                    widthMeasureSpec,
                    lp.leftMargin + lp.rightMargin,
                    adjustedHeightSpec,
                    lp.topMargin + lp.bottomMargin
                )
            } else {
                measureChildWithMargins(
                    child,
                    widthMeasureSpec,
                    lp.leftMargin + lp.rightMargin,
                    heightMeasureSpec,
                    lp.topMargin + lp.bottomMargin
                )
            }

            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin

            maxWidth = maxOf(maxWidth, childWidth)
            maxHeight = maxOf(maxHeight, childHeight)

            childState = combineMeasuredStates(childState, child.measuredState)
        }

        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom

        val resolvedWidth = resolveSizeAndState(maxWidth, widthMeasureSpec, childState)
        val resolvedHeight = resolveSizeAndState(
            maxHeight,
            heightMeasureSpec,
            childState shl MEASURED_HEIGHT_STATE_SHIFT
        )

        setMeasuredDimension(resolvedWidth, resolvedHeight)

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        secondaryBottomSheetCurrentY =
            1000//(measuredHeight + marginTop + getBottomInsets()) - tabPlaceHolderHeight().roundToInt()
    }

    private fun getBottomInsets() = insets?.systemWindowInsetBottom ?: 0
    private fun getTopInsets() = insets?.stableInsetTop ?: 0

    private fun tabPlaceHolderHeight(): Float {
        return context.resources.getDimension(R.dimen.tabLayoutHeight)
    }


    class PrimaryBottomSheetLayoutParams : LayoutParams {
        companion object {
            val ATTR = intArrayOf(
                android.R.attr.layout_weight,
            )
        }

        @SuppressLint("ResourceType")
        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            c.obtainStyledAttributes(attrs, ATTR).let {
                it.hasValue(0)
                it.recycle()
            }
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams) : super(source)

        fun copy(width: Int, height: Int): PrimaryBottomSheetLayoutParams {
            return this.apply {
                this.width = width
                this.height = height
            }
        }
    }
}