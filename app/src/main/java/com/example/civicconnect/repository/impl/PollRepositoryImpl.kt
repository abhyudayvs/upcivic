package com.example.civicconnect.repository.impl

import com.example.civicconnect.model.Poll
import com.example.civicconnect.repository.PollRepository
import com.example.civicconnect.util.Constants.COLLECTION_POLLS
import com.example.civicconnect.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PollRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : PollRepository {

    override fun getActivePolls(): Flow<Result<List<Poll>>> = callbackFlow {
        trySend(Result.Loading)

        val currentTime = System.currentTimeMillis()
        val listener = firestore.collection(COLLECTION_POLLS)
            .whereGreaterThan("endDate", currentTime)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                val polls = snapshot?.documents?.mapNotNull {
                    it.toObject(Poll::class.java)?.copy(id = it.id)
                } ?: emptyList()

                trySend(Result.Success(polls))
            }

        awaitClose {
            listener.remove()
        }
    }

    override fun getPollById(pollId: String): Flow<Result<Poll>> = callbackFlow {
        trySend(Result.Loading)

        val listener = firestore.collection(COLLECTION_POLLS)
            .document(pollId)
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

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun vote(pollId: String, optionIndex: Int): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val pollRef = firestore.collection(COLLECTION_POLLS).document(pollId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(pollRef)
                val poll = snapshot.toObject(Poll::class.java)
                    ?: throw Exception("Poll not found")

                if (poll.hasVoted(userId)) {
                    throw Exception("User has already voted")
                }

                if (poll.isExpired()) {
                    throw Exception("Poll has expired")
                }

                val updatedVotes = poll.votes.toMutableMap()
                updatedVotes[userId] = optionIndex

                val optionVoteCounts = poll.options.indices.map { index ->
                    updatedVotes.count { it.value == index }
                }

                transaction.update(
                    pollRef,
                    mapOf(
                        "votes" to updatedVotes,
                        "optionVoteCounts" to optionVoteCounts
                    )
                )
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createPoll(poll: Poll): Result<String> {
        return try {
            val docRef = firestore.collection(COLLECTION_POLLS)
                .add(poll)
                .await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 