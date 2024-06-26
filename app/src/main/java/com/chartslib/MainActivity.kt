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
import androidx.compose.runtime.mutableStateListOf
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
import com.chartslib.charts.bar.components.BarChart
import com.chartslib.charts.bar.models.Axis
import com.chartslib.charts.bar.models.BarChartConfiguration
import com.chartslib.charts.bar.models.BarColumnModel
import com.chartslib.charts.cartesian.components.Cartesian
import com.chartslib.charts.cartesian.components.CartesianSystem
import com.chartslib.charts.cartesian.components.CartesianSystemPreferences
import com.chartslib.charts.cartesian.components.HorizontalLine
import com.chartslib.charts.cartesian.components.HorizontalLineAlignment
import com.chartslib.charts.cartesian.components.InitialGridLines
import com.chartslib.charts.cartesian.components.LabelPreferences
import com.chartslib.charts.cartesian.components.LineStyle
import com.chartslib.charts.cartesian.components.SizePreferences
import com.chartslib.charts.cartesian.components.VerticalLine
import com.chartslib.charts.cartesian.components.VerticalLineAlignment
import com.chartslib.charts.donut.components.DonutChart
import com.chartslib.charts.donut.models.DonutSegmentModel
import com.chartslib.charts.line.components.LineChart
import com.chartslib.charts.line.components.LineChartWidth
import com.chartslib.charts.line.models.LineModel
import com.chartslib.charts.line.models.LineType
import com.chartslib.charts.line.models.Point
import com.chartslib.ui.theme.Azure
import com.chartslib.ui.theme.ChartsLibTheme
import com.chartslib.ui.theme.Palettes1
import com.chartslib.ui.theme.Palettes2
import com.chartslib.ui.theme.Palettes3
import com.chartslib.ui.theme.Palettes4
import com.chartslib.ui.theme.Palettes5
import com.chartslib.ui.theme.Water

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChartsLibTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    //BAR CHART
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
                        BarChart(
                            chartConfiguration = BarChartConfiguration(
                                modifier = Modifier
                                    .background(Color.White)
                                    .padding(10.dp)
                                    .height(200.dp),
                                columns = listOfData,
                                paddingBetweenColumns = 0.3f,
                                axisX = Axis(
                                    steps = listOfData.size,
                                    label = { month[it] },
                                    maxValue = listOfData.size.toFloat()
                                )
                            )
                        )

                        val secondData = remember {
                            mutableListOf(
                                BarColumnModel(value = 5f, color = Palettes1),
                                BarColumnModel(value = 8f, color = Palettes2),
                                BarColumnModel(value = 10f, color = Palettes3),
                                BarColumnModel(value = 3f, color = Palettes4),
                                BarColumnModel(value = 5f, color = Palettes5),
                                BarColumnModel(value = 8f, color = Palettes1),
                                BarColumnModel(value = 8f, color = Palettes2),
                            )
                        }
                        BarChart(
                            chartConfiguration = BarChartConfiguration(
                                modifier = Modifier
                                    .background(Color.White)
                                    .padding(10.dp)
                                    .height(200.dp),
                                columns = secondData,
                                paddingBetweenColumns = 0.3f,
                                axisX = Axis(
                                    steps = listOfData.size,
                                    label = { month[it] },
                                    maxValue = listOfData.size.toFloat()
                                ),
                                shouldMatchWidth = false,
                                minColumnWidth = 1000.dp
                            )
                        )


                            //DONUT
                        val pieDataList = remember {
                            mutableStateListOf(
                                DonutSegmentModel(value = 29, color = Palettes1),
                                DonutSegmentModel(value = 10, color = Palettes5),
                                DonutSegmentModel(value = 21, color = Palettes2),
                                DonutSegmentModel(value = 32, color = Palettes3, isSelected = true),
                                DonutSegmentModel(value = 10, color = Palettes4)
                            )
                        }

                        DonutChart(
                            modifier = Modifier.height(200.dp),
                            segments = pieDataList,
                            onSegmentSelected = {})


                        val pieDataList2 = remember {
                            mutableStateListOf(
                                DonutSegmentModel(
                                    value = 29,
                                    color = Azure,
                                    isSelected = true
                                ),
                                DonutSegmentModel(
                                    value = 10,
                                    color = Water
                                ),
                                DonutSegmentModel(
                                    value = 21,
                                    color = Azure,
                                    isSelected = true
                                ),
                                DonutSegmentModel(
                                    value = 32,
                                    color = Water
                                )
                            )
                        }

                        DonutChart(
                            modifier = Modifier.height(400.dp),
                            segments = pieDataList2,
                            onSegmentSelected = {})


                        //LINEAR
                        val points = listOf(
                            Point(0f, 1f),
                            Point(1f, 2f),
                            Point(2f, 1f),
                            Point(3f, 3f),
                            Point(4f, 2f),
                            Point(5f, 3f),
                        )

                        val points2 = listOf(
                            Point(0f, 3f),
                            Point(1f, 2f),
                            Point(2f, 3f),
                            Point(3f, 2f),
                            Point(4f, 1f),
                            Point(5f, 3f),
                        )

                        val points3 = listOf(
                            Point(0f, 2f),
                            Point(1f, 1f),
                            Point(2f, 2f),
                            Point(3f, 1f),
                            Point(4f, 3f),
                            Point(5f, 3f),
                            )

                        LineChart(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(10.dp)
                                .height(200.dp),
                            lines = listOf(
                                LineModel(
                                    points = points2,
                                    lineType = LineType.CURVED,
                                    color = Palettes5
                                ),
                                LineModel(
                                    points = points,
                                    lineType = LineType.CURVED,
                                    color = Palettes1
                                )
                            ),
                            horizontalGridLines = listOf(
                                HorizontalLine(
                                    isLineVisible = false,
                                    label = "90",
                                    positionInPercentage = 0f,
                                    labelAlignment = HorizontalLineAlignment.UNDER_LINE
                                ),
                                HorizontalLine(
                                    isLineVisible = false,
                                    label = "0",
                                    positionInPercentage = 100f,
                                    labelAlignment = HorizontalLineAlignment.ABOVE_LINE
                                ),
                                HorizontalLine(
                                    isLineVisible = true,
                                    label = "30",
                                    positionInPercentage = 30f,
                                    labelAlignment = HorizontalLineAlignment.CENTERED
                                ),
                                HorizontalLine(
                                    isLineVisible = true,
                                    label = "60",
                                    positionInPercentage = 60f,
                                    labelAlignment = HorizontalLineAlignment.CENTERED
                                )
                            ),

                            verticalGridLines = listOf(
                                VerticalLine(
                                    isLineVisible = false,
                                    label = "May",
                                    positionInPercentage = 0f,
                                    labelAlignment = VerticalLineAlignment.AFTER_LINE
                                ),
                                VerticalLine(
                                    isLineVisible = true,
                                    label = "April",
                                    positionInPercentage = 50f,
                                    labelAlignment = VerticalLineAlignment.CENTERED
                                ), VerticalLine(
                                    isLineVisible = false,
                                    label = "March",
                                    positionInPercentage = 100f,
                                    labelAlignment = VerticalLineAlignment.BEFORE_LINE
                                )
                            ),
                            dotRadius = 0.dp
                        )

                        LineChart(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(10.dp)
                                .height(200.dp),
                            lines = listOf(
                                LineModel(
                                    points = points2,
                                    lineType = LineType.CURVED,
                                    color = Azure
                                ),
                                LineModel(
                                    points = points,
                                    lineType = LineType.STRAIGHT,
                                    color = Azure
                                )
                            ),
                            horizontalGridLines = listOf(
                                HorizontalLine(
                                    isLineVisible = false,
                                    label = "90",
                                    positionInPercentage = 0f,
                                    labelAlignment = HorizontalLineAlignment.UNDER_LINE
                                ),
                                HorizontalLine(
                                    isLineVisible = false,
                                    label = "0",
                                    positionInPercentage = 100f,
                                    labelAlignment = HorizontalLineAlignment.ABOVE_LINE
                                ),
                                HorizontalLine(
                                    isLineVisible = true,
                                    label = "30",
                                    positionInPercentage = 30f,
                                    labelAlignment = HorizontalLineAlignment.CENTERED
                                ),
                                HorizontalLine(
                                    isLineVisible = true,
                                    label = "60",
                                    positionInPercentage = 60f,
                                    labelAlignment = HorizontalLineAlignment.CENTERED
                                )
                            ),

                            verticalGridLines = listOf(
                                VerticalLine(
                                    isLineVisible = false,
                                    label = "May",
                                    positionInPercentage = 0f,
                                    labelAlignment = VerticalLineAlignment.AFTER_LINE
                                ),
                                VerticalLine(
                                    isLineVisible = true,
                                    label = "April",
                                    positionInPercentage = 50f,
                                    labelAlignment = VerticalLineAlignment.CENTERED
                                ), VerticalLine(
                                    isLineVisible = false,
                                    label = "March",
                                    positionInPercentage = 100f,
                                    labelAlignment = VerticalLineAlignment.BEFORE_LINE
                                )
                            ),
                            lineChartSizePreferences=  LineChartWidth.MatchParent,
                            dotRadius = 0.dp
                        )

                        LineChart(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(10.dp)
                                .height(200.dp),
                            lines = listOf(
                                LineModel(
                                    points = points2,
                                    lineType = LineType.STRAIGHT,
                                    color = Palettes3
                                ),
                                LineModel(
                                    points = points,
                                    lineType = LineType.STRAIGHT,
                                    color = Azure
                                ),
                                LineModel(
                                    points = points3,
                                    lineType = LineType.STRAIGHT,
                                    color = Palettes1
                                )
                            ),
                            horizontalGridLines = listOf(
                                HorizontalLine(
                                    isLineVisible = false,
                                    label = "90",
                                    positionInPercentage = 0f,
                                    labelAlignment = HorizontalLineAlignment.UNDER_LINE,
                                ),
                                HorizontalLine(
                                    isLineVisible = false,
                                    label = "0",
                                    positionInPercentage = 100f,
                                    labelAlignment = HorizontalLineAlignment.ABOVE_LINE,
                                ),
                                HorizontalLine(
                                    isLineVisible = true,
                                    label = "30",
                                    positionInPercentage = 30f,
                                    labelAlignment = HorizontalLineAlignment.CENTERED
                                ),
                                HorizontalLine(
                                    isLineVisible = true,
                                    label = "60",
                                    positionInPercentage = 60f,
                                    labelAlignment = HorizontalLineAlignment.CENTERED
                                )
                            ),

                            verticalGridLines = listOf(
                                VerticalLine(
                                    isLineVisible = false,
                                    label = "May",
                                    positionInPercentage = 0f,
                                    labelAlignment = VerticalLineAlignment.AFTER_LINE
                                ),
                                VerticalLine(
                                    isLineVisible = true,
                                    label = "April",
                                    positionInPercentage = 50f,
                                    labelAlignment = VerticalLineAlignment.CENTERED
                                ), VerticalLine(
                                    isLineVisible = false,
                                    label = "March",
                                    positionInPercentage = 100f,
                                    labelAlignment = VerticalLineAlignment.BEFORE_LINE
                                )
                            )
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