package com.example.civicconnect.ui.admin.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.data.repository.UserRepository
import com.example.civicconnect.model.User
import com.example.civicconnect.model.UserStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageUsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentFilter: UserStatus? = null
    private var currentQuery: String? = null
    private var searchJob: Job? = null

    init {
        loadUsers()
    }

    fun refreshUsers() {
        loadUsers()
    }

    fun filterUsers(status: UserStatus?) {
        currentFilter = status
        loadUsers()
    }

    fun searchUsers(query: String?) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce search
            currentQuery = query
            loadUsers()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _users.value = userRepository.getUsers(currentFilter, currentQuery)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun suspendUser(userId: String) {
        updateUserStatus(userId, UserStatus.SUSPENDED)
    }

    fun banUser(userId: String) {
        updateUserStatus(userId, UserStatus.BANNED)
    }

    fun activateUser(userId: String) {
        updateUserStatus(userId, UserStatus.ACTIVE)
    }

    private fun updateUserStatus(userId: String, status: UserStatus) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                userRepository.updateUserStatus(userId, status)
                loadUsers()
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