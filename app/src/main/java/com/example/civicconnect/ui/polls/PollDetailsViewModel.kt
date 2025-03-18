package com.example.civicconnect.ui.polls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.data.repository.PollRepository
import com.example.civicconnect.model.Poll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PollDetailsViewModel @Inject constructor(
    private val pollRepository: PollRepository
) : ViewModel() {

    private val _poll = MutableLiveData<Poll>()
    val poll: LiveData<Poll> = _poll

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadPoll(pollId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _poll.value = pollRepository.getPoll(pollId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun vote(optionIndex: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _poll.value?.let { poll ->
                    if (!poll.hasUserVoted && !poll.isExpired) {
                        val updatedPoll = pollRepository.vote(poll.id, optionIndex)
                        _poll.value = updatedPoll
                    } else {
                        _error.value = if (poll.isExpired) "Poll has expired" else "You have already voted"
                    }
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