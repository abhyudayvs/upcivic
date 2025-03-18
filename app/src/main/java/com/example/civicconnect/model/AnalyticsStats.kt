package com.example.civicconnect.model

data class AnalyticsStats(
    val totalUsers: Int,
    val activeUsers: Int,
    val totalIssues: Int,
    val totalPolls: Int,
    val engagementRate: Float,
    val resolutionRate: Float,
    val userGrowth: Float,
    val issueGrowth: Float,
    val pollParticipation: Float
) 