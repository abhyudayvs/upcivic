package com.example.civicconnect.repository

import com.example.civicconnect.model.Poll
import com.example.civicconnect.util.Result
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebasePollRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PollRepository {

    private val pollsCollection = firestore.collection("polls")

    override fun getActivePolls(): Flow<Result<List<Poll>>> = callbackFlow {
        trySend(Result.Loading)

        val currentTime = System.currentTimeMillis()
        val subscription = pollsCollection
            .whereGreaterThan("endDate", currentTime)
            .orderBy("endDate")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val polls = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Poll::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(Result.Success(polls))
            }

        awaitClose { subscription.remove() }
    }

    override fun getPollById(pollId: String): Flow<Result<Poll>> = callbackFlow {
        trySend(Result.Loading)

        val subscription = pollsCollection.document(pollId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val poll = snapshot?.toObject(Poll::class.java)?.copy(id = snapshot.id)
                if (poll != null) {
                    trySend(Result.Success(poll))
                } else {
                    trySend(Result.Error(Exception("Poll not found")))
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun createPoll(
        question: String,
        description: String,
        options: List<String>,
        endDate: Long?
    ): Result<Poll> = try {
        val poll = Poll(
            question = question,
            description = description,
            options = options,
            endDate = endDate,
            votes = options.associateWith { 0 }
        )

        val docRef = pollsCollection.add(poll).await()
        Result.Success(poll.copy(id = docRef.id))
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun submitVote(pollId: String, option: String): Result<Unit> = try {
        pollsCollection.document(pollId)
            .update("votes.$option", FieldValue.increment(1))
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
} 