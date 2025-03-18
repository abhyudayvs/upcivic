package com.example.civicconnect.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.data.repository.AdminRepository
import com.example.civicconnect.model.AdminStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _adminStats = MutableLiveData<AdminStats>()
    val adminStats: LiveData<AdminStats> = _adminStats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadAdminStats()
    }

    private fun loadAdminStats() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _adminStats.value = adminRepository.getAdminStats()
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