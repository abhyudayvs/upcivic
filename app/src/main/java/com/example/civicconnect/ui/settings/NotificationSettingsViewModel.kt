package com.example.civicconnect.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.notification.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _notificationsEnabled = MutableLiveData(notificationManager.isNotificationsEnabled)
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationManager.isNotificationsEnabled = enabled
            _notificationsEnabled.value = enabled
        }
    }
} 