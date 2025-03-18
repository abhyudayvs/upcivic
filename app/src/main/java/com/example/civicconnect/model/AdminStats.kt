package com.example.civicconnect.model

data class AdminStats(
    val totalIssues: Int,
    val totalPolls: Int,
    val totalUsers: Int,
    val totalReports: Int,
    val activeUsers: Int,
    val issuesThisWeek: Int,
    val pollsThisWeek: Int
) 