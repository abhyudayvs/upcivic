package com.example.civicconnect.repository.impl

import com.example.civicconnect.model.Issue
import com.example.civicconnect.repository.IssueRepository
import com.example.civicconnect.util.Constants.COLLECTION_ISSUES
import com.example.civicconnect.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IssueRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IssueRepository {

    override fun getIssues(): Flow<Result<List<Issue>>> = callbackFlow {
        trySend(Result.Loading)

        val listener = firestore.collection(COLLECTION_ISSUES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val issues = snapshot?.documents?.mapNotNull {
                    it.toObject(Issue::class.java)?.copy(id = it.id)
                } ?: emptyList()

                trySend(Result.Success(issues))
            }

        awaitClose {
            listener.remove()
        }
    }

    override fun getIssueById(issueId: String): Flow<Result<Issue>> = callbackFlow {
        trySend(Result.Loading)

        val listener = firestore.collection(COLLECTION_ISSUES)
            .document(issueId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val issue = snapshot?.toObject(Issue::class.java)?.copy(id = snapshot.id)
                if (issue != null) {
                    trySend(Result.Success(issue))
                } else {
                    trySend(Result.Error(Exception("Issue not found")))
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun createIssue(issue: Issue): Result<String> {
        return try {
            val docRef = firestore.collection(COLLECTION_ISSUES)
                .add(issue)
                .await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateIssue(issue: Issue): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_ISSUES)
                .document(issue.id)
                .set(issue)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteIssue(issueId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_ISSUES)
                .document(issueId)
                .delete()
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun toggleSupport(issueId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val issueRef = firestore.collection(COLLECTION_ISSUES).document(issueId)
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(issueRef)
                val issue = snapshot.toObject(Issue::class.java)
                    ?: throw Exception("Issue not found")

                val supporters = issue.supporters.toMutableSet()
                if (supporters.contains(userId)) {
                    supporters.remove(userId)
                } else {
                    supporters.add(userId)
                }

                transaction.update(issueRef, 
                    mapOf(
                        "supporters" to supporters,
                        "supportCount" to supporters.size
                    )
                )
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 