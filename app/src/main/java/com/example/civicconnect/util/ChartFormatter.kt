package com.example.civicconnect.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

object ChartFormatter {
    class DateAxisFormatter(private val pattern: String = "MM/dd") : ValueFormatter() {
        private val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return dateFormat.format(Date(value.toLong()))
        }
    }

    class PercentFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "%.1f%%".format(value)
        }
    }

    class CompactNumberFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return when {
                value >= 1_000_000 -> "%.1fM".format(value / 1_000_000)
                value >= 1_000 -> "%.1fK".format(value / 1_000)
                else -> "%.0f".format(value)
            }
        }
    }
} 