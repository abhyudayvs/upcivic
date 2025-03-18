package com.example.civicconnect.ui.issues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.data.repository.IssueRepository
import com.example.civicconnect.model.Issue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueDetailsViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : ViewModel() {

    private val _issue = MutableLiveData<Issue>()
    val issue: LiveData<Issue> = _issue

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadIssue(issueId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _issue.value = issueRepository.getIssue(issueId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSupport() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _issue.value?.let { issue ->
                    val updatedIssue = issueRepository.toggleSupport(issue.id)
                    _issue.value = updatedIssue
                }
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