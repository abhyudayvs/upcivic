package com.example.civicconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.model.User
import com.example.civicconnect.repository.AuthRepository
import com.example.civicconnect.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                _user.value = user
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.signIn(email, password)) {
                is Result.Success -> {
                    _user.value = result.data
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {
                    // Already handled by _isLoading
                }
            }
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.signUp(email, password, name)) {
                is Result.Success -> {
                    _user.value = result.data
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {
                    // Already handled by _isLoading
                }
            }
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _user.value = null
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                authRepository.sendPasswordResetEmail(email).await()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 