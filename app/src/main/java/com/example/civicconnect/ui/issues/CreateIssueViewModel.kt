package com.example.civicconnect.ui.issues

import android.net.Uri
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
class CreateIssueViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : ViewModel() {

    private val _selectedImage = MutableLiveData<Uri?>()
    val selectedImage: LiveData<Uri?> = _selectedImage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun setSelectedImage(uri: Uri) {
        _selectedImage.value = uri
    }

    fun createIssue(title: String, description: String, category: String, location: String) {
        if (!validateInputs(title, description, category, location)) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val imageUrl = _selectedImage.value?.let { uri ->
                    issueRepository.uploadImage(uri)
                }

                val issue = Issue(
                    title = title,
                    description = description,
                    category = category,
                    location = location,
                    photoUrl = imageUrl
                )

                issueRepository.createIssue(issue)
                _isSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateInputs(
        title: String,
        description: String,
        category: String,
        location: String
    ): Boolean {
        if (title.isBlank() || description.isBlank() || category.isBlank() || location.isBlank()) {
            _error.value = "Please fill all fields"
            return false
        }
        return true
    }

    fun clearError() {
        _error.value = null
    }
} 