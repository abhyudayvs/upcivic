package com.example.civicconnect.util

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieDataSet

object ChartTheme {
    fun applyLineChartTheme(chart: LineChart, context: Context) {
        with(chart) {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = getThemeTextColor(context)
                valueFormatter = ChartFormatter.DateAxisFormatter()
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.argb(40, 128, 128, 128)
                textColor = getThemeTextColor(context)
                valueFormatter = ChartFormatter.CompactNumberFormatter()
            }

            axisRight.isEnabled = false
            legend.textColor = getThemeTextColor(context)

            animateXY(750, 750)
        }
    }

    fun applyPieChartTheme(chart: PieChart, context: Context) {
        with(chart) {
            description.isEnabled = false
            setDrawEntryLabels(false)
            setTouchEnabled(true)
            legend.apply {
                textColor = getThemeTextColor(context)
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
            }
            animateY(1000)
        }
    }

    fun applyBarChartTheme(chart: BarChart, context: Context) {
        with(chart) {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(false)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = getThemeTextColor(context)
                valueFormatter = ChartFormatter.DateAxisFormatter()
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.argb(40, 128, 128, 128)
                textColor = getThemeTextColor(context)
                valueFormatter = ChartFormatter.CompactNumberFormatter()
            }

            axisRight.isEnabled = false
            legend.textColor = getThemeTextColor(context)

            animateY(750)
        }
    }

    fun applyLineDataSetTheme(dataSet: LineDataSet, context: Context) {
        with(dataSet) {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            setDrawCircles(false)
            lineWidth = 2f
            fillAlpha = 50
            color = ChartColors.LINE_COLORS[0]
            fillColor = ChartColors.LINE_COLORS[0]
            valueTextColor = getThemeTextColor(context)
            valueFormatter = ChartFormatter.CompactNumberFormatter()
        }
    }

    fun applyPieDataSetTheme(dataSet: PieDataSet) {
        with(dataSet) {
            colors = ChartColors.MATERIAL_COLORS.toList()
            valueLineColor = Color.LTGRAY
            valueLinePart1Length = 0.3f
            valueLinePart2Length = 0.7f
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueFormatter = ChartFormatter.PercentFormatter()
        }
    }

    fun applyBarDataSetTheme(dataSet: BarDataSet, context: Context) {
        with(dataSet) {
            color = ChartColors.BAR_COLORS[0]
            valueTextColor = getThemeTextColor(context)
            valueFormatter = ChartFormatter.CompactNumberFormatter()
        }
    }

    private fun getThemeTextColor(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorPrimary, value, true)
        return context.getColor(value.resourceId)
    }
} 