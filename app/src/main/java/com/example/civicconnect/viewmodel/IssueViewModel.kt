package com.example.civicconnect.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.model.Issue
import com.example.civicconnect.repository.IssueRepository
import com.example.civicconnect.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : ViewModel() {

    private val _issues = MutableLiveData<List<Issue>>()
    val issues: LiveData<List<Issue>> = _issues

    private val _userIssues = MutableLiveData<List<Issue>>()
    val userIssues: LiveData<List<Issue>> = _userIssues

    private val _currentIssue = MutableLiveData<Issue?>()
    val currentIssue: LiveData<Issue?> = _currentIssue

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadIssues()
    }

    private fun loadIssues() {
        viewModelScope.launch {
            issueRepository.getIssues().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _issues.value = result.data
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.message
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun loadUserIssues(userId: String) {
        viewModelScope.launch {
            issueRepository.getUserIssues(userId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _userIssues.value = result.data
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.message
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun loadIssueDetails(issueId: String) {
        viewModelScope.launch {
            issueRepository.getIssueById(issueId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _currentIssue.value = result.data
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.message
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun reportIssue(
        title: String,
        description: String,
        category: String,
        location: String,
        photoUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = issueRepository.createIssue(title, description, category, location, photoUri)) {
                is Result.Success -> {
                    _isLoading.value = false
                    loadIssues() // Refresh the issues list
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                    _isLoading.value = false
                }
                else -> {}
            }
        }
    }

    fun updateIssueStatus(issueId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = issueRepository.updateIssueStatus(issueId, status)) {
                is Result.Success -> {
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                    _isLoading.value = false
                }
                else -> {}
            }
        }
    }

    fun createIssue(issue: Issue) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = issueRepository.createIssue(issue)) {
                is Result.Success -> {
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

    fun toggleSupport(issueId: String) {
        viewModelScope.launch {
            when (val result = issueRepository.toggleSupport(issueId)) {
                is Result.Success -> {
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {
                    // No loading state needed for this operation
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 