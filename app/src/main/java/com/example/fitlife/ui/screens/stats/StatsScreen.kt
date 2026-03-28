package com.example.fitlife.ui.screens.stats

import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.data.local.dao.CenterCount
import com.example.fitlife.data.local.dao.DayCount
import com.example.fitlife.data.local.dao.MonthCount
import com.example.fitlife.viewmodel.StatsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun StatsScreen() {
    val vm: StatsViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium
        )

        if (state.isLoading) {
            Text("Loading...")
            return@Column
        }

        ChartCard(title = "Visits by month") {
            BarChartView(
                data = state.monthlyVisits,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ChartCard(title = "Top 5 centers by visits") {
            PieChartView(
                data = state.topCenters,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ChartCard(title = "Cumulative visits (last 30 days)") {
            LineChartView(
                data = state.dailyVisitsLast30Days,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

@Composable
private fun BarChartView(
    data: List<MonthCount>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                minimumHeight = 700
                description.isEnabled = false
                setFitBars(true)
                axisRight.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                animateY(800)
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.count.toFloat())
            }

            val labels = data.map { it.yearMonth }

            val dataSet = BarDataSet(entries, "Visits by month").apply {
                valueTextSize = 12f
            }

            val barData = BarData(dataSet).apply {
                barWidth = 0.6f
            }

            chart.data = barData
            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = -35f
            }

            chart.axisLeft.axisMinimum = 0f
            chart.invalidate()
        }
    )
}

@Composable
private fun PieChartView(
    data: List<CenterCount>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {
                minimumHeight = 700
                description.isEnabled = false
                setUsePercentValues(false)
                setEntryLabelTextSize(12f)
                centerText = "Top 5"
                setCenterTextSize(16f)
                setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                animateY(800)
            }
        },
        update = { chart ->
            val entries = data.map {
                PieEntry(it.count.toFloat(), it.centerName)
            }

            val dataSet = PieDataSet(entries, "Visits by center").apply {
                valueTextSize = 12f

                colors = mutableListOf<Int>().apply {
                    addAll(ColorTemplate.MATERIAL_COLORS.toList())
                    addAll(ColorTemplate.COLORFUL_COLORS.toList())
                    addAll(ColorTemplate.JOYFUL_COLORS.toList())
                }
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
private fun LineChartView(
    data: List<DayCount>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                minimumHeight = 700
                description.isEnabled = false
                axisRight.isEnabled = false
                setDrawGridBackground(false)
                animateX(800)
                legend.isEnabled = false
            }
        },
        update = { chart ->
            var runningTotal = 0f

            val cumulativeEntries = data.mapIndexed { index, item ->
                runningTotal += item.count.toFloat()
                Entry(index.toFloat(), runningTotal)
            }

            val labels = data.map { day ->
                if (day.day.length >= 10) {
                    day.day.substring(5) // MM-dd
                } else {
                    day.day
                }
            }

            val dataSet = LineDataSet(cumulativeEntries, "Cumulative visits").apply {
                valueTextSize = 10f
                lineWidth = 2f
                circleRadius = 3.5f
                setDrawValues(true)
            }

            chart.data = LineData(dataSet)

            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = -45f
                setLabelCount(labels.size.coerceAtMost(6), false)
            }

            chart.axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
            }

            chart.invalidate()
        }
    )
}