package com.example.civicconnect.repository

import android.net.Uri
import com.example.civicconnect.model.Issue
import com.example.civicconnect.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseIssueRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : IssueRepository {

    private val issuesCollection = firestore.collection("issues")

    override fun getIssues(): Flow<Result<List<Issue>>> = callbackFlow {
        trySend(Result.Loading)

        val subscription = issuesCollection
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val issues = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Issue::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(Result.Success(issues))
            }

        awaitClose { subscription.remove() }
    }

    override fun getUserIssues(userId: String): Flow<Result<List<Issue>>> = callbackFlow {
        trySend(Result.Loading)

        val subscription = issuesCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val issues = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Issue::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(Result.Success(issues))
            }

        awaitClose { subscription.remove() }
    }

    override fun getIssueById(issueId: String): Flow<Result<Issue>> = callbackFlow {
        trySend(Result.Loading)

        val subscription = issuesCollection.document(issueId)
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

        awaitClose { subscription.remove() }
    }

    override suspend fun createIssue(
        title: String,
        description: String,
        category: String,
        location: String,
        photoUri: Uri?
    ): Result<Issue> = try {
        val photoUrl = photoUri?.let { uri ->
            val photoRef = storage.reference.child("issues/${UUID.randomUUID()}")
            photoRef.putFile(uri).await()
            photoRef.downloadUrl.await().toString()
        }

        val issue = Issue(
            title = title,
            description = description,
            category = category,
            location = location,
            photoUrl = photoUrl
        )

        val docRef = issuesCollection.add(issue).await()
        Result.Success(issue.copy(id = docRef.id))
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateIssueStatus(issueId: String, status: String): Result<Unit> = try {
        issuesCollection.document(issueId)
            .update("status", status)
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
} 