package com.example.civicconnect.ui.admin.polls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.data.repository.PollRepository
import com.example.civicconnect.model.Poll
import com.example.civicconnect.model.PollStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagePollsViewModel @Inject constructor(
    private val pollRepository: PollRepository
) : ViewModel() {

    private val _polls = MutableLiveData<List<Poll>>()
    val polls: LiveData<List<Poll>> = _polls

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentFilter: PollStatus? = null

    init {
        loadPolls()
    }

    fun refreshPolls() {
        loadPolls()
    }

    fun filterPolls(status: PollStatus?) {
        currentFilter = status
        loadPolls()
    }

    private fun loadPolls() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _polls.value = pollRepository.getPolls(currentFilter)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun activatePoll(pollId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                pollRepository.updatePollStatus(pollId, PollStatus.ACTIVE)
                loadPolls()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun closePoll(pollId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                pollRepository.updatePollStatus(pollId, PollStatus.CLOSED)
                loadPolls()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePoll(pollId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                pollRepository.deletePoll(pollId)
                loadPolls()
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