package com.example.civicconnect.repository

import com.example.civicconnect.model.Representative
import com.example.civicconnect.util.Result
import kotlinx.coroutines.flow.Flow

interface RepresentativeRepository {
    fun getRepresentatives(): Flow<Result<List<Representative>>>
    fun getRepresentativeById(id: String): Flow<Result<Representative>>
    suspend fun sendMessage(representativeId: String, message: String): Result<Unit>
} 