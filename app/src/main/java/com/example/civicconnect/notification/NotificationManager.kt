package com.example.civicconnect.notification

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences,
    private val firebaseMessaging: FirebaseMessaging
) {

    var isNotificationsEnabled: Boolean
        get() = preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) {
            preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()
            if (value) {
                subscribeToTopics()
            } else {
                unsubscribeFromTopics()
            }
        }

    suspend fun getToken(): String? {
        return try {
            firebaseMessaging.token.await()
        } catch (e: Exception) {
            null
        }
    }

    private fun subscribeToTopics() {
        NOTIFICATION_TOPICS.forEach { topic ->
            firebaseMessaging.subscribeToTopic(topic)
        }
    }

    private fun unsubscribeFromTopics() {
        NOTIFICATION_TOPICS.forEach { topic ->
            firebaseMessaging.unsubscribeFromTopic(topic)
        }
    }

    companion object {
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private val NOTIFICATION_TOPICS = listOf(
            "issues",
            "polls",
            "representatives"
        )
    }
} 