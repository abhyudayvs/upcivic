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
class CreatePollViewModel @Inject constructor(
    private val pollRepository: PollRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun createPoll(question: String, description: String, endDate: String, options: List<String>) {
        if (!validateInputs(question, description, endDate, options)) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val poll = Poll(
                    question = question,
                    description = description,
                    endDate = endDate,
                    options = options
                )
                pollRepository.createPoll(poll)
                _isSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateInputs(
        question: String,
        description: String,
        endDate: String,
        options: List<String>
    ): Boolean {
        if (question.isBlank() || description.isBlank() || endDate.isBlank()) {
            _error.value = "Please fill all fields"
            return false
        }
        if (options.size < 2) {
            _error.value = "Please add at least 2 options"
            return false
        }
        return true
    }

    fun clearError() {
        _error.value = null
    }
} 