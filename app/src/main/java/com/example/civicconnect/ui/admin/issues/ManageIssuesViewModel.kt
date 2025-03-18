package com.example.civicconnect.ui.admin.issues

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
class ManageIssuesViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : ViewModel() {

    private val _issues = MutableLiveData<List<Issue>>()
    val issues: LiveData<List<Issue>> = _issues

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentFilter: IssueStatus? = null

    init {
        loadIssues()
    }

    fun refreshIssues() {
        loadIssues()
    }

    fun filterIssues(status: IssueStatus?) {
        currentFilter = status
        loadIssues()
    }

    private fun loadIssues() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _issues.value = issueRepository.getIssues(currentFilter)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveIssue(issueId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                issueRepository.updateIssueStatus(issueId, IssueStatus.APPROVED)
                loadIssues()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectIssue(issueId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                issueRepository.updateIssueStatus(issueId, IssueStatus.REJECTED)
                loadIssues()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteIssue(issueId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                issueRepository.deleteIssue(issueId)
                loadIssues()
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