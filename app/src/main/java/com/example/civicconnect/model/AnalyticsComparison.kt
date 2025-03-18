package com.example.civicconnect.model

data class AnalyticsComparison(
    val currentPeriod: PeriodData,
    val previousPeriod: PeriodData,
    val percentageChange: Float
) {
    data class PeriodData(
        val startDate: Date,
        val endDate: Date,
        val value: Float
    )
} 