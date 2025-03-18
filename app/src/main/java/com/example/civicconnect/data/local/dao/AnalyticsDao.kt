package com.example.civicconnect.data.local.dao

import androidx.room.*
import com.example.civicconnect.data.local.entity.AnalyticsDataEntity
import com.example.civicconnect.data.local.entity.CategoryStatsEntity
import java.util.Date

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM analytics_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getAnalyticsData(startDate: Date, endDate: Date): List<AnalyticsDataEntity>

    @Query("SELECT * FROM category_stats WHERE lastUpdated >= :since")
    suspend fun getCategoryStats(since: Date): List<CategoryStatsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyticsData(data: List<AnalyticsDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryStats(stats: List<CategoryStatsEntity>)

    @Query("DELETE FROM analytics_data WHERE date < :date")
    suspend fun deleteOldAnalyticsData(date: Date)

    @Query("DELETE FROM category_stats WHERE lastUpdated < :date")
    suspend fun deleteOldCategoryStats(date: Date)

    @Query("SELECT MAX(lastUpdated) FROM analytics_data")
    suspend fun getLastAnalyticsUpdate(): Date?

    @Query("SELECT MAX(lastUpdated) FROM category_stats")
    suspend fun getLastCategoryStatsUpdate(): Date?
} 