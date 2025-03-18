package com.example.civicconnect.repository.impl

import com.example.civicconnect.model.User
import com.example.civicconnect.repository.AuthRepository
import com.example.civicconnect.util.Constants.COLLECTION_USERS
import com.example.civicconnect.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userDoc = firestore.collection(COLLECTION_USERS)
                .document(authResult.user?.uid ?: "")
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User data not found")
                
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(
                id = authResult.user?.uid ?: "",
                email = email,
                name = name
            )
            
            firestore.collection(COLLECTION_USERS)
                .document(user.id)
                .set(user)
                .await()
                
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { firebaseUser ->
                firestore.collection(COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        trySend(document.toObject(User::class.java))
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } ?: run {
                trySend(null)
            }
        }

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override fun isUserSignedIn(): Boolean = auth.currentUser != null
} 