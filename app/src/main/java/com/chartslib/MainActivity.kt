package com.chartslib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chartslib.charts.bar.models.BarColumnModel
import com.chartslib.charts.cartesian.components.Cartesian
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.LabelPreferences
import com.chartslib.charts.cartesian.components.LineStyle
import com.chartslib.charts.cartesian.components.SizePreferences
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.cartesian.components.VerticalLineAlignment
import com.chartslib.charts.line.components.LineChart
import com.chartslib.charts.line.models.LineModel
import com.chartslib.charts.line.models.LineType
import com.chartslib.charts.line.models.Point
import com.chartslib.ui.theme.ChartsLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChartsLibTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val listOfData =
                        remember {
                            mutableListOf(
                                BarColumnModel(value = 5f),
                                BarColumnModel(value = 8f),
                                BarColumnModel(value = 10f),
                                BarColumnModel(value = 3f),
                                BarColumnModel(value = 5f),
                                BarColumnModel(value = 8f),
                                BarColumnModel(value = 8f),
                            )
                        }

                    val month = listOf(
                        "April",
                        "March",
                        "June",
                        "October",
                        "November",
                        "December",
                        "July"
                    )

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
//                        BarChart(
//                            chartConfiguration = BarChartConfiguration(
//                                modifier = Modifier
//                                    .background(Color.White)
//                                    .padding(10.dp)
//                                    .height(200.dp),
//                                columns = listOfData,
//                                paddingBetweenColumns = 0.3f,
//                                axisX = Axis(
//                                    steps = listOfData.size,
//                                    label = { month[it] },
//                                    maxValue = listOfData.size.toFloat()
//                                )
//                            )
//                        )
//
//                        val horizontalLines1 =
//                            Collections.nCopies(5, HorizontalLine())
//                        val verticalLines1 =
//                            Collections.nCopies(5, VerticalLine())
//
//                        CartesianSystem(
//                            modifier = Modifier
//                                .background(Color.White)
//                                .padding(10.dp)
//                                .height(200.dp),
//                            cartesianSysPrefs = CartesianSystemPreferences(
//                                horizontalLines = horizontalLines1,
//                                verticalLines = verticalLines1
//                            )
//                        )
//
//                        val months = listOf(
//                            "April",
//                            "March",
//                            "June",
//                            "October",
//                            "November"
//                        )
//                        val horizontalLines2 = mutableListOf<HorizontalLine>()
//                        repeat(5) {
//                            horizontalLines2.add(HorizontalLine(label = (it * 100).toString())) // every next label is 100 greater than previous
//                        }
//
//                        val verticalLines2 = mutableListOf<VerticalLine>()
//                        repeat(5) {
//                            verticalLines2.add(VerticalLine(label = months[it], lineBrush = SolidColor(Color.Red)))
//                        }
//
//                        CartesianSystem(
//                            modifier = Modifier
//                                .background(Color.White)
//                                .padding(10.dp)
//                                .height(200.dp),
//                            cartesianSysPrefs = CartesianSystemPreferences(
//                                horizontalLines = horizontalLines2,
//                                verticalLines = verticalLines2,
//
//                                horizontalLabelsPreferences = LabelSizePreferences(
//                                    style = TextStyle(fontSize = 10.sp),
//                                    labelAndChartPadding = 20.dp,
//                                    maxWidth = 30.dp
//                                ),
//                                verticalLabelsPreferences = LabelSizePreferences(
//                                    style = TextStyle(fontSize = 10.sp),
//                                    labelAndChartPadding = 10.dp
//                                ),
//                            )
//                        )
//
                        val horizontalLines3 = mutableListOf<HorizontalLine>()
                        repeat(7) {
                            if (it == 0) // make the first one stroke
                                horizontalLines3.add(HorizontalLine(lineStyle = LineStyle.StrokeLine))
                            else
                                horizontalLines3.add(HorizontalLine())
                        }

                        val verticalLines3 = mutableListOf<VerticalLine>()
                        repeat(4) {
                            if (it == 3) {                          //if it last display text before line
                                verticalLines3.add(
                                    VerticalLine(
                                        label = (it * 100).toString(),
                                        labelAlignment = VerticalLineAlignment.BEFORE_LINE
                                    )
                                )
                            } else if (it == 0) {                   //if it first display text after line
                                verticalLines3.add(
                                    VerticalLine(
                                        label = (it * 100).toString(),
                                        labelAlignment = VerticalLineAlignment.AFTER_LINE,
                                        lineStyle = LineStyle.StrokeLine
                                    )
                                )
                            } else {
                                verticalLines3.add(VerticalLine()) //no text and default line
                            }
                        }

                        CartesianSystem(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(10.dp)
                                .height(200.dp),
                            cartesianSysPrefs = CartesianSystemPreferences(
                                horizontalLines = horizontalLines3,
                                verticalLines = verticalLines3,

                                horizontalLabelsPreferences = LabelPreferences(
                                    style = TextStyle(fontSize = 10.sp),
                                    labelAndChartPadding = 5.dp
                                ),
                                verticalLabelsPreferences = LabelPreferences(
                                    style = TextStyle(fontSize = 10.sp),
                                    maxWidth = 20.dp,
                                ),
                            )
                        )
//
//                        val horizontalLines = mutableListOf<HorizontalLine>()
//                        repeat(7) {
//                            if (true)
//                                horizontalLines.add(
//                                    HorizontalLine(
//                                        lineStyle = LineStyle.StrokeLine,
//                                        label = it.toString()
//                                    )
//                                )
//                            else
//                                horizontalLines.add(HorizontalLine())
//                        }
//
//                        val verticalLines = mutableListOf<VerticalLine>()
//                        repeat(9) {
//                            if (it % 2 == 0)
//                                verticalLines.add(VerticalLine())
//                            else
//                                verticalLines.add(
//                                    VerticalLine(
//                                        isLineVisible = false,
//                                        label = (it * 100).toString(),
//                                        labelAlignment = VerticalLineAlignment.CENTERED
//                                    )
//                                )
//                        }
////
//                        CartesianSystem(
//                            modifier = Modifier
//                                .background(Color.White)
//                                .padding(10.dp)
//                                .height(200.dp),
//                            cartesianSysPrefs = CartesianSystemPreferences(
//                                horizontalLines = horizontalLines,
//                                verticalLines = verticalLines,
//                                horizontalLabelsPreferences = LabelSizePreferences(
//                                    style = TextStyle(fontSize = 10.sp),
//                                    labelAndChartPadding = 5.dp
//                                ),
//                                verticalLabelsPreferences = LabelSizePreferences(
//                                    style = TextStyle(fontSize = 10.sp),
//                                    labelAndChartPadding = 5.dp
//                                ),
//
//                                horizontalExtraPadding = Padding(bottom = 10.dp, top = 10.dp),
//                                verticalExtraPadding = Padding(start = 10.dp, end = 10.dp)
//                            )
//                        ) { topLeft, width, height, drawScope ->
//
//                        }
//
//                        LineChart(
//                            modifier = Modifier
//                                .background(Color.White)
//                                .padding(10.dp)
//                                .height(200.dp),
//                            lines = LineModel(
//                                points = listOf(
//                                    Point(1f, 2f),
//                                    Point(2f, 5f),
//                                    Point(3f, 5f),
//                                    Point(4f, 10f),
//                                    Point(5f, 10f),
//                                    Point(6f, 11f),
//                                )
//                            )
//                        )

                        val points = listOf(
                            Point(0f, 1f),
                            Point(1f, 2f),
                            Point(2f, 1f),
                        )

                        val points2 = listOf(
                            Point(0f, 1f),
                            Point(1f, 2f),
                            Point(2f, 1f),
                        )
//                        val max = points.maxOf { it.y }
//                        val min = points.minOf { it.y }
//
//                        val steps = 5
//
//                        val diff = max - min
//                        val diffPerValue = diff / (steps - 1)
//
//                        val h = HorizontalLine.Builder()
//                            .setSteps(5).setLabels { position ->
//                                (min + diffPerValue * position).toString()
//                            }.build()
//
//
//                        val listOfData2 =
//                            rememberMutableStateListOf(
//                                DonutSegmentModel(
//                                    value = 29,
//                                    color = Palettes1
//                                ),
//                                DonutSegmentModel(
//                                    value = 10,
//                                    color = Palettes5
//                                ),
//                                DonutSegmentModel(
//                                    value = 21,
//                                    color = Palettes2
//                                ),
//                                DonutSegmentModel(
//                                    value = 32,
//                                    color = Palettes3
//                                ),
//                                DonutSegmentModel(
//                                    value = 10,
//                                    color = Palettes4
//                                )
//                            )
//                        val pieDataList = remember {
//                            mutableStateListOf(
//                                DonutSegmentModel(
//                                    value = 29,
//                                    color = Palettes1
//                                ),
//                                DonutSegmentModel(
//                                    value = 10,
//                                    color = Palettes5
//                                ),
//                                DonutSegmentModel(
//                                    value = 21,
//                                    color = Palettes2
//                                ),
//                                DonutSegmentModel(
//                                    value = 32,
//                                    color = Palettes3
//                                ),
//                                DonutSegmentModel(
//                                    value = 10,
//                                    color = Palettes4
//                                )
//                            )
//                        }
//
//                        LineChart(
//                            modifier = Modifier
//                                .background(Color.White)
//                                .padding(10.dp)
//                                .height(200.dp),
//                            lines = listOf(
//                                LineModel(points = points2, lineType = LineType.CURVED)
//                            )
//                        )
//
//                        LineChart(
//                            modifier = Modifier
//                                .background(Color.White)
//                                .padding(10.dp)
//                                .height(200.dp),
//                            lines = listOf(
//                                LineModel(points = points2, lineType = LineType.CURVED)
//                            ),
//                            cartesianSystemPreferences = CartesianSystemPreferences(
//                                horizontalLines = HorizontalLine.Builder().setSteps(3).build(),
//                                verticalLines = VerticalLine.Builder().setUnspecifiedLinesAmount(2)
//                                    .setSpecifiedLinesAmount(points.size - 1) { index ->
//                                        val list = points2
//                                        val points = points2
//
//                                        VerticalLine(
//                                            lineBrush = SolidColor(Color.LightGray),
//                                            lineThickness = 1.dp,
//                                            positionInPercentage = (points[index].x / points.maxOf { it.x }) * 100
//                                        )
//                                    }
//                                    .setLabels { it.toString() }.build(),
//                                sizePreferences = SizePreferences.FixedToWidth,
//                                verticalExtraPadding = Padding(end = 3.dp),
//                                horizontalExtraPadding = Padding(end = 3.dp)
//                            ),
//                            dotRadius = 3.dp
//                        )

                        Cartesian(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(200.dp)
                                .clipToBounds(),
                            sizePreferences = SizePreferences.SpecificSize(800.dp),
                            horizontalLines = listOf(

                                HorizontalLine(
                                    lineBrush = SolidColor(Color.Red),
                                    lineThickness = 1.dp,
                                    positionInPercentage = 0f,
                                    label = "asdf"
                                ),
                                HorizontalLine(
                                    lineBrush = SolidColor(Color.Red),
                                    lineThickness = 1.dp,
                                    positionInPercentage = 99f,
                                    label = "asdf"
                                )
                            ),
                            verticalLines = listOf(
                                VerticalLine(
                                    lineBrush = SolidColor(Color.Red),
                                    lineThickness = 1.dp,
                                    positionInPercentage = 100f,
                                    label = "asdf"
                                ),

                                VerticalLine(
                                    lineBrush = SolidColor(Color.Red),
                                    lineThickness = 1.dp,
                                    positionInPercentage = 0f,
                                    label = "asdf"
                                )
                            )
                        )
                        LineChart(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(10.dp)
                                .height(200.dp),
                            lines = listOf(
                                LineModel(points = points2, lineType = LineType.CURVED),
                                LineModel(
                                    points = points,
                                    lineType = LineType.CURVED,
                                    color = Color.Red
                                )
                            ),
                            cartesianSystemPreferences = CartesianSystemPreferences(
                                horizontalLines = listOf(HorizontalLine(label = "іваф", positionInPercentage = 50f), ),
                                verticalLines = VerticalLine.Builder()
                                    .setSpecifiedLinesAmount(points.size - 1) { index ->
                                        if (index != 0 || index != points.size - 1) {
                                            val list = points2
                                            val points = points2

                                            VerticalLine(
                                                isLineVisible = true,
                                                label = "Hello",
                                                positionInPercentage = 50f
                                            )
                                        } else {
                                            VerticalLine(
                                                isLineVisible = false
                                            )
                                        }

                                    }
                                    .setLabels { it.toString() }.build(),
                                sizePreferences = SizePreferences.SpecificSize(2000.dp)
                            ),
                            dotRadius = 3.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun <T : Any> rememberMutableStateListOf(vararg elements: T): SnapshotStateList<T> {
    return rememberSaveable(
        saver = listSaver(
            save = { stateList ->
                if (stateList.isNotEmpty()) {
                    val first = stateList.first()
                    if (!canBeSaved(first)) {
                        throw IllegalStateException("${first::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
                    }
                }
                stateList.toList()
            },
            restore = { it.toMutableStateList() }
        )
    ) {
        elements.toList().toMutableStateList()
    }
}