package com.example.civicconnect.repository

import com.example.civicconnect.model.Poll
import com.example.civicconnect.util.Result
import kotlinx.coroutines.flow.Flow

interface PollRepository {
    fun getActivePolls(): Flow<Result<List<Poll>>>
    fun getPollById(pollId: String): Flow<Result<Poll>>
    suspend fun vote(pollId: String, optionIndex: Int): Result<Unit>
    suspend fun createPoll(poll: Poll): Result<String>
} 