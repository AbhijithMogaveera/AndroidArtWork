package com.abhijith.customviewcollection.screens

import androidx.annotation.ColorRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.abhijith.customviewcollection.R
import com.abhijith.cylindergraph.DemoData
import com.abhijith.cylindergraph.CylinderSectionData
import com.abhijith.cylindergraph.CylinderHover
import com.abhijith.cylindergraph.CylinderGraphView

data class SectionDataWrapper(
    val title: String,
    val subTitle: String,
    val cylinderSectionData: CylinderSectionData,
    var point: LayoutCoordinates? = null
)

@Composable
fun CylinderGraphComp() {
    val ss: ScrollState = rememberScrollState()
    val list = remember {
        DemoData.items.mapIndexed { index, sectionData ->
            SectionDataWrapper(
                "item ${index + 1}",
                "${sectionData.percentage}% Space occupied",
                cylinderSectionData = sectionData
            )
        }
    }
    var drawFromY by remember {
        mutableStateOf(0f)
    }
    var toY by remember {
        mutableStateOf(0f)
    }
    var drawColor by remember {
        mutableStateOf(R.color.black)
    }
    var isAnySectionSelected by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxWidth().systemBarsPadding()) {
        var view: CylinderGraphView? = null
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(1f)
        ) {
            AndroidView(
                factory = { context ->
                    CylinderGraphView(context).apply {
                        setData(list.map {
                            it.cylinderSectionData
                        })
                        cylinderHover = object : CylinderHover {

                            override fun onEnter() {
                                isAnySectionSelected = true
                            }

                            override fun onMove(
                                x: Float,
                                y: Float,
                                cylinderSectionData: CylinderSectionData
                            ) {
                                drawFromY = y
                                val firstOrNull = list.firstOrNull {
                                    it.cylinderSectionData === cylinderSectionData
                                }
                                toY = (firstOrNull?.point?.positionInParent()?.y
                                    ?: 0).toFloat() - ss.value
                                drawColor = cylinderSectionData.color
                            }

                            override fun onExit() {
                                isAnySectionSelected = false
                            }

                        }
                        animateStack()
                    }
                },
                modifier = Modifier
                    .padding(vertical = 60.dp)
                    .width(130.dp),
                update = {
                    view = it
                }

            )
            if (isAnySectionSelected)
                PathDrawer(
                    Modifier
                        .padding(vertical = 130.dp)
                        .width(100.dp)
                        .fillMaxHeight(),
                    drawFromY,
                    toY = toY,
                    drawColor
                )
            else
                Spacer(
                    Modifier
                        .padding(vertical = 50.dp)
                        .width(100.dp)
                        .fillMaxHeight(),
                )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 130.dp)
                    .fillMaxHeight()
                    .verticalScroll(state = ss)
            ) {
                list.asReversed().forEach {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = colorResource(id = it.cylinderSectionData.color),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .onGloballyPositioned { c ->
                                it.point = c
                            }
                    ) {
                        Row(modifier = Modifier) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                            ) {
                                Text(
                                    maxLines = 1,
                                    text = it.title + " • " + it.subTitle,
                                )
                            }/*
                            IconButton(onClick = {

                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_android_black_24dp),
                                    contentDescription = ""
                                )
                            }*/
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                view?.animateStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = "Animate",
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
fun PathDrawer(
    modifier: Modifier,
    fromY: Float,
    toY: Float,
    @ColorRes
    color: Int = R.color.black
) {
    val resolvedColor = colorResource(id = color)
    Canvas(modifier = modifier, onDraw = {
        val start = Offset(0f, fromY)
        val end = Offset(size.width, toY)
        drawCircle(
            color = resolvedColor,
            radius = 3.dp.toPx(),
            center = start
        )
        drawCircle(
            color = resolvedColor,
            radius = 3.dp.toPx(),
            center = end
        )
        drawLine(
            color = resolvedColor,
            start = start,
            end = start.copy(x = start.x + 30.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = resolvedColor,
            start = start.copy(x = start.x + 30.dp.toPx()),
            end = end.copy(x = end.x - 30.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = resolvedColor,
            start = end.copy(x = end.x - 30.dp.toPx()),
            end = end,
            strokeWidth = 2.dp.toPx()
        )
    })
}
