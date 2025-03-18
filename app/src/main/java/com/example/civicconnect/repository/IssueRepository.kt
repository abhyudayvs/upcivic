package com.example.civicconnect.repository

import android.net.Uri
import com.example.civicconnect.model.Issue
import com.example.civicconnect.util.Result
import kotlinx.coroutines.flow.Flow

interface IssueRepository {
    fun getIssues(): Flow<Result<List<Issue>>>
    fun getUserIssues(userId: String): Flow<Result<List<Issue>>>
    fun getIssueById(issueId: String): Flow<Result<Issue>>
    suspend fun createIssue(issue: Issue): Result<String>
    suspend fun updateIssue(issue: Issue): Result<Unit>
    suspend fun deleteIssue(issueId: String): Result<Unit>
    suspend fun toggleSupport(issueId: String): Result<Unit>
} 