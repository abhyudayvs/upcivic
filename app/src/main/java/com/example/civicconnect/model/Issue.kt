package com.example.civicconnect.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Issue(
    val id: String,
    val title: String,
    val description: String,
    val photoUrl: String?,
    val status: IssueStatus,
    val createdAt: Date,
    val userId: String,
    val location: String?,
    val category: String?
) {
    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(createdAt)
    }
} 