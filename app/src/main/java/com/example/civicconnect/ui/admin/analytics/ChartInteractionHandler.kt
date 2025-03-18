package com.example.civicconnect.ui.admin.analytics

import android.content.Context
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.example.civicconnect.util.ChartFormatter
import com.example.civicconnect.util.ChartTheme
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.civicconnect.R
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.ChartTouchListener
import android.view.MotionEvent
import android.util.Log

class ChartInteractionHandler(
    private val context: Context,
    private val onDataPointSelected: (Date, AnalyticsDataPoint) -> Unit
) {
    private var showTrendLine = true
    private var movingAverages: List<Float> = emptyList()
    private var highlightPeaks = false
    private var currentZoomLevel = 1f
    private val maxZoomLevel = 5f
    private val minZoomLevel = 0.5f
    private val annotations = mutableMapOf<Float, String>()
    private var selectedEntry: Entry? = null

    fun setupLineChartInteraction(chart: LineChart) {
        with(chart) {
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let { entry ->
                        val date = Date(entry.x.toLong())
                        val value = AnalyticsDataPoint(
                            value = entry.y,
                            label = "Active Users",
                            date = date
                        )
                        onDataPointSelected(date, value)
                    }
                }

                override fun onNothingSelected() {}
            })

            setScaleEnabled(true)
            setPinchZoom(true)
            isDoubleTapToZoomEnabled = true

            axisLeft.apply {
                setDrawGridLines(true)
                enableGridDashedLine(10f, 10f, 0f)
                axisMinimum = 0f
            }

            xAxis.apply {
                setDrawGridLines(false)
                valueFormatter = ChartFormatter.DateAxisFormatter()
            }

            legend.isEnabled = true
            description.isEnabled = false

            onChartGestureListener = object : OnChartGestureListener {
                override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                    // Handle gesture start
                }

                override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                    when (lastPerformedGesture) {
                        ChartTouchListener.ChartGesture.PINCH_ZOOM -> {
                            currentZoomLevel = scaleX
                        }
                        else -> {}
                    }
                }

                override fun onChartLongPressed(me: MotionEvent?) {
                    me?.let { 
                        onLongPress(chart, it.x, it.y)
                    }
                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
                override fun onChartDoubleTapped(me: MotionEvent?) {}
                override fun onChartSingleTapped(me: MotionEvent?) {}
                override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}
            }
        }
    }

    fun setupBarChartInteraction(chart: BarChart) {
        with(chart) {
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let { entry ->
                        val date = Date(entry.x.toLong())
                        val value = AnalyticsDataPoint(
                            value = entry.y,
                            label = "Poll Votes",
                            date = date
                        )
                        onDataPointSelected(date, value)
                    }
                }

                override fun onNothingSelected() {}
            })

            setScaleEnabled(true)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = true

            axisLeft.apply {
                setDrawGridLines(true)
                enableGridDashedLine(10f, 10f, 0f)
                axisMinimum = 0f
            }

            xAxis.apply {
                setDrawGridLines(false)
                valueFormatter = ChartFormatter.DateAxisFormatter()
            }

            legend.isEnabled = true
            description.isEnabled = false
        }
    }

    fun updateTrendLine(chart: LineChart, data: List<Entry>, movingAverages: List<Float>) {
        this.movingAverages = movingAverages
        
        if (showTrendLine) {
            val trendEntries = data.mapIndexed { index, entry ->
                Entry(entry.x, movingAverages.getOrNull(index) ?: entry.y)
            }
            
            val trendDataSet = LineDataSet(trendEntries, "Trend").apply {
                color = ContextCompat.getColor(context, R.color.trend_line)
                setDrawCircles(false)
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(false)
            }

            chart.data = LineData(listOf(chart.data.getDataSetByIndex(0), trendDataSet))
        } else {
            chart.data = LineData(chart.data.getDataSetByIndex(0))
        }
        
        chart.invalidate()
    }

    fun setTrendLineVisible(visible: Boolean) {
        showTrendLine = visible
    }

    fun zoomIn(chart: LineChart) {
        if (currentZoomLevel < maxZoomLevel) {
            currentZoomLevel *= 1.5f
            chart.zoom(1.5f, 1.5f, chart.width / 2f, chart.height / 2f)
        }
    }

    fun zoomOut(chart: LineChart) {
        if (currentZoomLevel > minZoomLevel) {
            currentZoomLevel /= 1.5f
            chart.zoom(0.75f, 0.75f, chart.width / 2f, chart.height / 2f)
        }
    }

    fun resetZoom(chart: LineChart) {
        currentZoomLevel = 1f
        chart.fitScreen()
    }

    fun setHighlightPeaks(chart: LineChart, highlight: Boolean) {
        highlightPeaks = highlight
        if (highlight) {
            val data = chart.data.getDataSetByIndex(0).entries
            val peaks = findPeaks(data)
            highlightPeakPoints(chart, peaks)
        } else {
            clearHighlights(chart)
        }
    }

    private fun findPeaks(entries: List<Entry>): List<Entry> {
        if (entries.size < 3) return emptyList()
        
        return entries.windowed(3).mapNotNull { window ->
            val (prev, current, next) = window
            if (current.y > prev.y && current.y > next.y) {
                current
            } else {
                null
            }
        }
    }

    private fun highlightPeakPoints(chart: LineChart, peaks: List<Entry>) {
        val highlights = peaks.map { Highlight(it.x, it.y, 0) }
        chart.highlightValues(highlights.toTypedArray())
    }

    private fun clearHighlights(chart: LineChart) {
        chart.highlightValues(null)
    }

    fun onLongPress(chart: LineChart, x: Float, y: Float) {
        val h = chart.getHighlightByTouchPoint(x, y)
        h?.let { highlight ->
            val entry = chart.data.getDataSetByIndex(highlight.dataSetIndex)
                .getEntryForXValue(highlight.x, highlight.y)
            
            selectedEntry = entry
            showContextMenu(chart, entry, Date(entry.x.toLong()))
        }
    }

    private fun showContextMenu(chart: LineChart, entry: Entry, date: Date) {
        ChartContextMenu(
            context = context,
            anchor = chart,
            entry = entry,
            date = date,
            onAddAnnotation = { e, text -> addAnnotation(chart, e, text) },
            onExportPoint = { exportDataPoint(it) },
            onCompareWithPrevious = { compareWithPrevious(it) }
        ).show()
    }

    private fun addAnnotation(chart: LineChart, entry: Entry, text: String) {
        annotations[entry.x] = text
        updateChartMarkers(chart)
    }

    private fun updateChartMarkers(chart: LineChart) {
        val dataSet = chart.data.getDataSetByIndex(0)
        dataSet.entries.forEach { entry ->
            if (annotations.containsKey(entry.x)) {
                entry.icon = context.getDrawable(R.drawable.ic_annotation)
            }
        }
        chart.invalidate()
    }

    private fun exportDataPoint(entry: Entry) {
        // Implement export functionality
        // For now, we can just log it
        Log.d("ChartInteraction", "Exporting data point: x=${entry.x}, y=${entry.y}")
    }

    private fun compareWithPrevious(date: Date) {
        // Implement comparison functionality
        // For now, we can just log it
        Log.d("ChartInteraction", "Comparing with previous: date=$date")
    }

    data class AnalyticsDataPoint(
        val value: Float,
        val label: String,
        val date: Date
    )
} 