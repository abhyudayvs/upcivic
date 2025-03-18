package com.example.civicconnect.ui.representatives

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.data.repository.RepresentativeRepository
import com.example.civicconnect.model.Representative
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepresentativesViewModel @Inject constructor(
    private val representativeRepository: RepresentativeRepository
) : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> = _representatives

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadRepresentatives()
    }

    fun refreshRepresentatives() {
        loadRepresentatives()
    }

    private fun loadRepresentatives() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _representatives.value = representativeRepository.getRepresentatives()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchByAddress(address: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _representatives.value = representativeRepository.searchByAddress(address)
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