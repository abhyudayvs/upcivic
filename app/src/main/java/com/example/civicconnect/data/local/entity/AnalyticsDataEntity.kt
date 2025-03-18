package com.example.civicconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "analytics_data")
data class AnalyticsDataEntity(
    @PrimaryKey
    val date: Date,
    val activeUsers: Int,
    val newIssues: Int,
    val resolvedIssues: Int,
    val pollVotes: Int,
    val lastUpdated: Date = Date()
)

@Entity(tableName = "category_stats")
data class CategoryStatsEntity(
    @PrimaryKey
    val category: String,
    val count: Int,
    val lastUpdated: Date = Date()
) 