package com.example.civicconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.model.Poll
import com.example.civicconnect.repository.PollRepository
import com.example.civicconnect.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PollViewModel @Inject constructor(
    private val pollRepository: PollRepository
) : ViewModel() {

    private val _polls = MutableLiveData<List<Poll>>()
    val polls: LiveData<List<Poll>> = _polls

    private val _currentPoll = MutableLiveData<Poll?>()
    val currentPoll: LiveData<Poll?> = _currentPoll

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadActivePolls()
    }

    private fun loadActivePolls() {
        viewModelScope.launch {
            pollRepository.getActivePolls().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _polls.value = result.data
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

    fun loadPollDetails(pollId: String) {
        viewModelScope.launch {
            pollRepository.getPollById(pollId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _currentPoll.value = result.data
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

    fun createPoll(poll: Poll) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = pollRepository.createPoll(poll)) {
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

    fun vote(pollId: String, optionIndex: Int) {
        viewModelScope.launch {
            when (val result = pollRepository.vote(pollId, optionIndex)) {
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