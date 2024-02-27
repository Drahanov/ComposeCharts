package com.chartslib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chartslib.charts.bar.components.BarChart
import com.chartslib.charts.bar.models.Axis
import com.chartslib.charts.bar.models.BarChartConfiguration
import com.chartslib.charts.bar.models.BarColumnModel
import com.chartslib.charts.bar.models.HorizontalLine
import com.chartslib.charts.bar.models.HorizontalLinesPattern
import com.chartslib.charts.bar.models.UtilityLines
import com.chartslib.charts.bar.models.VerticalLine
import com.chartslib.charts.bar.models.VerticalLinesPattern
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
                                    horizontalLines = HorizontalLinesPattern.FixedSize(
                                        lines = listOf(HorizontalLine())
                                    ),
                                    verticalLines = VerticalLinesPattern.FixedSize(
                                        lines = listOf()
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}