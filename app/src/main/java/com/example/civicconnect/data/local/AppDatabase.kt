package com.example.civicconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.civicconnect.data.local.converter.DateConverter
import com.example.civicconnect.data.local.dao.AnalyticsDao
import com.example.civicconnect.data.local.entity.AnalyticsDataEntity
import com.example.civicconnect.data.local.entity.CategoryStatsEntity

@Database(
    entities = [
        AnalyticsDataEntity::class,
        CategoryStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analyticsDao(): AnalyticsDao
    
    // ... other DAOs
} 