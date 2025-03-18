package com.example.civicconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.model.Representative
import com.example.civicconnect.repository.RepresentativeRepository
import com.example.civicconnect.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepresentativeViewModel @Inject constructor(
    private val repository: RepresentativeRepository
) : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> = _representatives

    private val _currentRepresentative = MutableLiveData<Representative>()
    val currentRepresentative: LiveData<Representative> = _currentRepresentative

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _messageSent = MutableLiveData<Boolean>()
    val messageSent: LiveData<Boolean> = _messageSent

    fun loadRepresentatives() {
        viewModelScope.launch {
            repository.getRepresentatives().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _representatives.value = result.data
                        _isLoading.value = false
                    }
                    is Result.Error -> {
                        _error.value = result.exception.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun loadRepresentativeDetails(id: String) {
        viewModelScope.launch {
            repository.getRepresentativeById(id).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _currentRepresentative.value = result.data
                        _isLoading.value = false
                    }
                    is Result.Error -> {
                        _error.value = result.exception.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun sendMessage(representativeId: String, message: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.sendMessage(representativeId, message)) {
                is Result.Success -> {
                    _messageSent.value = true
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

    fun clearError() {
        _error.value = null
    }

    fun resetMessageSent() {
        _messageSent.value = false
    }
} 