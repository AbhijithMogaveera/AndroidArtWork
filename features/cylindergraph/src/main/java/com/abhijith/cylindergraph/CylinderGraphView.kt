package com.abhijith.cylindergraph

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class CylinderGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var cylinderHover: CylinderHover? = null
    private val cylinder = CylinderDrawable()
    private var selectedWrapper: PairCylinderSectionAndRect? = null
    private var sectionListWithWrapper: List<PairCylinderSectionAndRect> = listOf()
    private var space = 0
    private var isInSelectionMode = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN-> {
                cylinderHover?.onEnter()
                isInSelectionMode = true
                detectSelectedRect(event)
            }
            MotionEvent.ACTION_MOVE -> {
                detectSelectedRect(event)
            }
            MotionEvent.ACTION_UP -> {
                isInSelectionMode = false
                selectedWrapper = null
                cylinderHover?.onExit()
                invalidate()
            }
        }
        return true
    }

    private fun detectSelectedRect(event: MotionEvent) {
        sectionListWithWrapper.onEach {

            if (
                canvasBound.contains(canvasBound.centerX(), event.y.toInt()) &&
                it.rect.contains(it.rect.centerX(), (event.y).toInt())
                && selectedWrapper != it
            ) {
                val toDp = 60f.toDp(context)
                cylinderHover?.onMove(
                    it.rect.centerX().toFloat(),
                    it.rect.centerY() - toDp,
                    it.data
                )
                selectedWrapper = it
                invalidate()
            }
        }
    }

    private fun distributeRectEvenly() {
        canvasBound.getSpaceDistributedStartByBottomWithGap(
            this,
            sectionListWithWrapper.map {
                it.data
            },
            space
        ) { index, rect, _ ->
            distributeSpace(rect, index)
        }
        invalidate()
    }

    fun setData(list: List<CylinderSectionData>) {
        sectionListWithWrapper = list.map {
            PairCylinderSectionAndRect(it)
        }
        distributeRectEvenly()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val start = paddingStart
        val top = paddingTop
        canvasBound.set(start, top, w, h)
        //intersection between two cylinders
        canvasBound.top += 130
        canvasBound.bottom -= 130
        calculateIndividualSectionData()

    }

    private fun calculateIndividualSectionData() {
        canvasBound.getSpaceDistributedStartByBottom(
            this,
            sectionListWithWrapper.map {
                it.data
            }
        ) { index, rect, _ ->
            distributeSpace(rect, index)
        }
    }

    private fun distributeSpace(rect: Rect, index: Int) {
        sectionListWithWrapper[index].rect.set(rect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInSelectionMode) {
            sectionListWithWrapper.forEachIndexed { _, sectionDataWrapper ->
                cylinder.invalidate(newBound = sectionDataWrapper.rect)
                cylinder.isEnabled = true
                cylinder.color = ContextCompat.getColor(context, sectionDataWrapper.data.color)
                cylinder.draw(canvas)
            }
        } else {
            sectionListWithWrapper.forEachIndexed { _, sectionDataWrapper ->
                if (selectedWrapper === sectionDataWrapper) {
                    cylinder.isEnabled = true
                    cylinder.invalidate(newBound = sectionDataWrapper.rect)
                    cylinder.color = ContextCompat.getColor(context, sectionDataWrapper.data.color)
                    cylinder.draw(canvas)
                } else {
                    cylinder.isEnabled = false
                    cylinder.invalidate(newBound = sectionDataWrapper.rect)
                    cylinder.color = ContextCompat.getColor(context, sectionDataWrapper.data.color)
                    cylinder.draw(canvas)
                }
            }
        }
    }

    private val valueAnimator = ValueAnimator()

    fun animateStack() {
        valueAnimator.apply {
            setFloatValues(1000f, 0f)
            duration = 1500
            addUpdateListener {
                space = (it.animatedValue as Float).toInt()
                distributeRectEvenly()
            }
        }.start()
    }

    val bound: Rect = Rect()

    val paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.BLACK
    }

    private val canvasBound: Rect = Rect()

}