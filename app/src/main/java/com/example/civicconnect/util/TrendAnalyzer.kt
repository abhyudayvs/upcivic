package com.example.civicconnect.util

import com.example.civicconnect.data.local.entity.AnalyticsDataEntity
import kotlin.math.abs

object TrendAnalyzer {
    data class Trend(
        val direction: Direction,
        val percentage: Float,
        val isSignificant: Boolean
    )

    enum class Direction {
        UP, DOWN, STABLE
    }

    fun analyzeTrend(
        data: List<AnalyticsDataEntity>,
        valueSelector: (AnalyticsDataEntity) -> Int,
        significanceThreshold: Float = 5f
    ): Trend {
        if (data.size < 2) return Trend(Direction.STABLE, 0f, false)

        val values = data.map { valueSelector(it).toFloat() }
        val firstValue = values.first()
        val lastValue = values.last()
        
        val changePercentage = if (firstValue != 0f) {
            ((lastValue - firstValue) / firstValue) * 100
        } else {
            0f
        }

        val direction = when {
            abs(changePercentage) < significanceThreshold -> Direction.STABLE
            changePercentage > 0 -> Direction.UP
            else -> Direction.DOWN
        }

        return Trend(
            direction = direction,
            percentage = abs(changePercentage),
            isSignificant = abs(changePercentage) >= significanceThreshold
        )
    }

    fun calculateMovingAverage(
        data: List<AnalyticsDataEntity>,
        valueSelector: (AnalyticsDataEntity) -> Int,
        windowSize: Int = 7
    ): List<Float> {
        val values = data.map { valueSelector(it).toFloat() }
        return values.windowed(windowSize, 1, true) { window ->
            window.average().toFloat()
        }
    }

    fun detectAnomalies(
        data: List<AnalyticsDataEntity>,
        valueSelector: (AnalyticsDataEntity) -> Int,
        threshold: Float = 2f
    ): List<Int> {
        if (data.size < 3) return emptyList()

        val values = data.map { valueSelector(it).toFloat() }
        val mean = values.average()
        val stdDev = calculateStandardDeviation(values)
        
        return values.mapIndexedNotNull { index, value ->
            if (abs(value - mean) > threshold * stdDev) index else null
        }
    }

    private fun calculateStandardDeviation(values: List<Float>): Float {
        val mean = values.average()
        val variance = values.map { (it - mean).pow(2) }.average()
        return kotlin.math.sqrt(variance).toFloat()
    }
} 