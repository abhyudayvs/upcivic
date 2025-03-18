package com.example.civicconnect.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Poll(
    val id: String,
    val title: String,
    val description: String,
    val options: List<PollOption>,
    val status: PollStatus,
    val createdAt: Date,
    val endDate: Date?,
    val totalVotes: Int,
    val createdBy: String
) {
    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(createdAt)
    }
}

data class PollOption(
    val id: String,
    val text: String,
    val votes: Int
) 