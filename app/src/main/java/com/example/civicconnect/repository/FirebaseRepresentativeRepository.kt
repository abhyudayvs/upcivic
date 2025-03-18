package com.example.civicconnect.repository

import com.example.civicconnect.model.Representative
import com.example.civicconnect.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepresentativeRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RepresentativeRepository {

    private val representativesCollection = firestore.collection("representatives")
    private val messagesCollection = firestore.collection("messages")

    override fun getRepresentatives(): Flow<Result<List<Representative>>> = callbackFlow {
        trySend(Result.Loading)

        val subscription = representativesCollection
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val representatives = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Representative::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(Result.Success(representatives))
            }

        awaitClose { subscription.remove() }
    }

    override fun getRepresentativeById(id: String): Flow<Result<Representative>> = callbackFlow {
        trySend(Result.Loading)

        val subscription = representativesCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val representative = snapshot?.toObject(Representative::class.java)?.copy(id = snapshot.id)
                if (representative != null) {
                    trySend(Result.Success(representative))
                } else {
                    trySend(Result.Error(Exception("Representative not found")))
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(representativeId: String, message: String): Result<Unit> = try {
        val messageData = hashMapOf(
            "representativeId" to representativeId,
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "messageId" to UUID.randomUUID().toString()
        )

        messagesCollection.add(messageData).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
} 