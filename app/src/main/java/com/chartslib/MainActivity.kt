package com.chartslib

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.chartslib.charts.bar.components.BarChart
import com.chartslib.charts.bar.models.Axis
import com.chartslib.charts.bar.models.BarChartConfiguration
import com.chartslib.charts.bar.models.BarColumnModel
import com.chartslib.charts.bar.models.HorizontalLine
import com.chartslib.charts.bar.models.HorizontalLineAlignment
import com.chartslib.charts.bar.models.UtilityLines
import com.chartslib.charts.bar.models.VerticalLine
import com.chartslib.charts.bar.models.VerticalLineAlignment
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

                    Column {
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
                                ),
                                utilityLines = UtilityLines(
                                    horizontalLines = listOf(
                                        HorizontalLine(label = "", alignment = HorizontalLineAlignment.UNDER_LINE),
                                        HorizontalLine(label = "4", alignment = HorizontalLineAlignment.CENTERED, isLineVisible = false),
                                        HorizontalLine(label = "", alignment = HorizontalLineAlignment.CENTERED),
                                        HorizontalLine(label = "4", alignment = HorizontalLineAlignment.CENTERED, isLineVisible = false),
                                        HorizontalLine(label = "", alignment = HorizontalLineAlignment.CENTERED),
                                        HorizontalLine(label = "4", alignment = HorizontalLineAlignment.CENTERED, isLineVisible = false),
                                        HorizontalLine(label = "", alignment = HorizontalLineAlignment.CENTERED),
                                        HorizontalLine(label = "4", alignment = HorizontalLineAlignment.CENTERED, isLineVisible = false),
                                        HorizontalLine(label = "", alignment = HorizontalLineAlignment.CENTERED),
                                        HorizontalLine(label = "4", alignment = HorizontalLineAlignment.CENTERED, isLineVisible = false),
                                        HorizontalLine(label = "", alignment = HorizontalLineAlignment.CENTERED),
                                        HorizontalLine(label = "4", alignment = HorizontalLineAlignment.CENTERED, isLineVisible = false),
                                        HorizontalLine(label = "", alignment = HorizontalLineAlignment.UNDER_LINE),
                                    ),
                                    verticalLines = listOf(
                                        VerticalLine(lineWidth = 1.dp, alignment = VerticalLineAlignment.BEFORE_LINE, label = "asфівафіафіваdf"),
                                        VerticalLine(lineWidth = 1.dp, alignment = VerticalLineAlignment.CENTERED, label = "asфівафіафіваdf"),
                                        VerticalLine(lineWidth = 1.dp, alignment = VerticalLineAlignment.CENTERED, label = "sadf"),
                                    )
                                )
                            )
                        )

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
//                                ),
//                                utilityLines = UtilityLines(
//                                    horizontalLines = listOf(
//                                        HorizontalLine(label = "1"),
//                                        HorizontalLine(label = "3"),
//                                        HorizontalLine(label = "1"),
//                                    ),
//                                    verticalLines = listOf(
//                                        VerticalLine(lineWidth = 1.dp),
//                                        VerticalLine()
//                                    )
//                                )
//                            )
//                        )

                        Box(
                            modifier = Modifier
                                .background(Color.Magenta)
                                .padding(10.dp)
                                .height(200.dp)
                                .fillMaxWidth()
                        ) {
                            val textMeasurer = rememberTextMeasurer()

                            val textStyle = androidx.compose.ui.text.TextStyle()
                            val measuredText = textMeasurer.measure(
                                AnnotatedString("A"),
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle.copy(background = Color.White),
                                constraints = Constraints(maxWidth = 100)
                            )

                            Log.d("Hello World", measuredText.size.height.toString())
                            Log.d("Hello World", measuredText.size.width.toString())
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawText(measuredText)
                            }
                        }
                    }
                }
            }
        }
    }
}