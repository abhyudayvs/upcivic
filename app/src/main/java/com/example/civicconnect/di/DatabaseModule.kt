package com.example.civicconnect.di

import android.content.Context
import androidx.room.Room
import com.example.civicconnect.data.local.AppDatabase
import com.example.civicconnect.data.local.dao.AnalyticsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "civic_connect.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAnalyticsDao(database: AppDatabase): AnalyticsDao {
        return database.analyticsDao()
    }
} 