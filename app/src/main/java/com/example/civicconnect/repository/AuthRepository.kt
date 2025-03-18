package com.example.civicconnect.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.civicconnect.model.User
import com.example.civicconnect.model.UserRole
import kotlinx.coroutines.tasks.await
import com.example.civicconnect.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signOut()
    fun getCurrentUser(): Flow<User?>
    fun isUserSignedIn(): Boolean
}

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun signUp(email: String, password: String, name: String, role: UserRole): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(
                id = authResult.user?.uid ?: "",
                email = email,
                name = name,
                role = role
            )
            
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .await()
                
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userDoc = firestore.collection("users")
                .document(authResult.user?.uid ?: "")
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User data not found")
                
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): Flow<User?> {
        // Implementation needed
        throw UnsupportedOperationException("Method not implemented")
    }

    fun isUserSignedIn(): Boolean {
        // Implementation needed
        throw UnsupportedOperationException("Method not implemented")
    }
} 