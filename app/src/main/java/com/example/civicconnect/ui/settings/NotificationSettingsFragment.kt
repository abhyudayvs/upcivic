package com.example.civicconnect.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.civicconnect.databinding.FragmentNotificationSettingsBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationSettingsFragment : BaseFragment<FragmentNotificationSettingsBinding>() {

    private val viewModel: NotificationSettingsViewModel by viewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNotificationSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            enableNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setNotificationsEnabled(isChecked)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.notificationsEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.enableNotificationsSwitch.isChecked = enabled
        }
    }
} 