package com.example.civicconnect.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentAdminPanelBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPanelFragment : BaseFragment<FragmentAdminPanelBinding>() {

    private val viewModel: AdminPanelViewModel by viewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAdminPanelBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            manageIssuesCard.setOnClickListener {
                findNavController().navigate(
                    AdminPanelFragmentDirections.actionAdminPanelToManageIssues()
                )
            }

            managePollsCard.setOnClickListener {
                findNavController().navigate(
                    AdminPanelFragmentDirections.actionAdminPanelToManagePolls()
                )
            }

            manageUsersCard.setOnClickListener {
                findNavController().navigate(
                    AdminPanelFragmentDirections.actionAdminPanelToManageUsers()
                )
            }

            analyticsCard.setOnClickListener {
                findNavController().navigate(
                    AdminPanelFragmentDirections.actionAdminPanelToAnalytics()
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.adminStats.observe(viewLifecycleOwner) { stats ->
            with(binding) {
                issuesCount.text = stats.totalIssues.toString()
                pollsCount.text = stats.totalPolls.toString()
                usersCount.text = stats.totalUsers.toString()
                reportsCount.text = stats.totalReports.toString()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }
} 