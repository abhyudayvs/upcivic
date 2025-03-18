package com.example.civicconnect.data.repository

import com.example.civicconnect.data.local.dao.AnalyticsDao
import com.example.civicconnect.data.local.entity.AnalyticsDataEntity
import com.example.civicconnect.data.local.entity.CategoryStatsEntity
import com.example.civicconnect.model.AnalyticsExportData
import com.example.civicconnect.model.AnalyticsStats
import com.example.civicconnect.util.ChartColors
import com.example.civicconnect.util.ChartFormatter
import com.github.mikephil.charting.data.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val analyticsDao: AnalyticsDao
) {
    private val cacheValidityPeriod = 30L * 60 * 1000 // 30 minutes

    suspend fun getUserActivityData(startDate: Date = getDefaultStartDate(), endDate: Date = Date()): LineData {
        val data = getAnalyticsData(startDate, endDate)
        val entries = data.map { Entry(it.date.time.toFloat(), it.activeUsers.toFloat()) }
        
        val dataSet = LineDataSet(entries, "Active Users")
        return LineData(dataSet)
    }

    suspend fun getIssueCategoriesData(startDate: Date = getDefaultStartDate(), endDate: Date = Date()): PieData {
        val stats = getCategoryStats()
        val entries = stats.map { PieEntry(it.count.toFloat(), it.category) }
        
        val dataSet = PieDataSet(entries, "Issue Categories")
        return PieData(dataSet)
    }

    suspend fun getPollEngagementData(startDate: Date = getDefaultStartDate(), endDate: Date = Date()): BarData {
        val data = getAnalyticsData(startDate, endDate)
        val entries = data.mapIndexed { index, entity ->
            BarEntry(index.toFloat(), entity.pollVotes.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Poll Votes")
        return BarData(dataSet)
    }

    suspend fun getAnalyticsStats(startDate: Date = getDefaultStartDate(), endDate: Date = Date()): AnalyticsStats {
        val data = getAnalyticsData(startDate, endDate)
        val latestData = data.lastOrNull() ?: return AnalyticsStats()
        
        return AnalyticsStats(
            totalUsers = latestData.activeUsers,
            activeUsers = data.last().activeUsers,
            totalIssues = data.sumOf { it.newIssues },
            totalPolls = data.sumOf { it.pollVotes },
            engagementRate = calculateEngagementRate(data),
            resolutionRate = calculateResolutionRate(data),
            userGrowth = calculateGrowthRate(data) { it.activeUsers },
            issueGrowth = calculateGrowthRate(data) { it.newIssues },
            pollParticipation = calculatePollParticipation(data)
        )
    }

    private suspend fun getAnalyticsData(startDate: Date, endDate: Date): List<AnalyticsDataEntity> {
        val shouldRefreshCache = shouldRefreshCache()
        
        return if (shouldRefreshCache) {
            val remoteData = fetchRemoteAnalyticsData(startDate, endDate)
            analyticsDao.insertAnalyticsData(remoteData)
            remoteData
        } else {
            analyticsDao.getAnalyticsData(startDate, endDate)
        }
    }

    private suspend fun getCategoryStats(): List<CategoryStatsEntity> {
        val shouldRefreshCache = shouldRefreshCache()
        
        return if (shouldRefreshCache) {
            val remoteStats = fetchRemoteCategoryStats()
            analyticsDao.insertCategoryStats(remoteStats)
            remoteStats
        } else {
            analyticsDao.getCategoryStats(getDefaultStartDate())
        }
    }

    private suspend fun shouldRefreshCache(): Boolean {
        val lastUpdate = analyticsDao.getLastAnalyticsUpdate()
        return lastUpdate == null || Date().time - lastUpdate.time > cacheValidityPeriod
    }

    private suspend fun fetchRemoteAnalyticsData(startDate: Date, endDate: Date): List<AnalyticsDataEntity> {
        val analyticsData = firestore.collection("analytics")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .await()

        return analyticsData.documents.mapNotNull { doc ->
            val date = doc.getTimestamp("date")?.toDate() ?: return@mapNotNull null
            AnalyticsDataEntity(
                date = date,
                activeUsers = doc.getLong("active_users")?.toInt() ?: 0,
                newIssues = doc.getLong("new_issues")?.toInt() ?: 0,
                resolvedIssues = doc.getLong("resolved_issues")?.toInt() ?: 0,
                pollVotes = doc.getLong("poll_votes")?.toInt() ?: 0
            )
        }
    }

    private suspend fun fetchRemoteCategoryStats(): List<CategoryStatsEntity> {
        val issuesSnapshot = firestore.collection("issues")
            .get()
            .await()

        return issuesSnapshot.documents
            .groupBy { doc -> doc.getString("category") ?: "Other" }
            .map { (category, docs) ->
                CategoryStatsEntity(
                    category = category,
                    count = docs.size
                )
            }
    }

    private fun getDefaultStartDate(): Date {
        return Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }.time
    }

    // Helper calculation methods...
    private fun calculateEngagementRate(data: List<AnalyticsDataEntity>): Float {
        if (data.isEmpty()) return 0f
        val latestData = data.last()
        return (latestData.activeUsers.toFloat() / data.maxOf { it.activeUsers }) * 100
    }

    private fun calculateResolutionRate(data: List<AnalyticsDataEntity>): Float {
        val totalIssues = data.sumOf { it.newIssues }
        val resolvedIssues = data.sumOf { it.resolvedIssues }
        return if (totalIssues > 0) {
            (resolvedIssues.toFloat() / totalIssues) * 100
        } else {
            0f
        }
    }

    private fun calculateGrowthRate(
        data: List<AnalyticsDataEntity>,
        valueSelector: (AnalyticsDataEntity) -> Int
    ): Float {
        if (data.size < 2) return 0f
        val oldValue = valueSelector(data.first())
        val newValue = valueSelector(data.last())
        return if (oldValue > 0) {
            ((newValue - oldValue).toFloat() / oldValue) * 100
        } else {
            0f
        }
    }

    private fun calculatePollParticipation(data: List<AnalyticsDataEntity>): Float {
        if (data.isEmpty()) return 0f
        val latestData = data.last()
        return (data.sumOf { it.pollVotes }.toFloat() / latestData.activeUsers)
    }
} 