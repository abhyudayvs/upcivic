package com.example.civicconnect.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val status: UserStatus,
    val joinDate: Date,
    val issueCount: Int,
    val pollCount: Int,
    val role: UserRole
) {
    fun getFormattedJoinDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return "Joined: ${dateFormat.format(joinDate)}"
    }
}

enum class UserRole {
    USER,
    MODERATOR,
    ADMIN
} 