package com.example.civicconnect.ui.admin.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentManageIssuesBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageIssuesFragment : BaseFragment<FragmentManageIssuesBinding>() {

    private val viewModel: ManageIssuesViewModel by viewModels()
    private val issuesAdapter = ManageIssuesAdapter(
        onIssueClick = { issue ->
            findNavController().navigate(
                ManageIssuesFragmentDirections.actionManageIssuesToIssueDetails(issue.id)
            )
        },
        onApproveClick = { issue ->
            viewModel.approveIssue(issue.id)
        },
        onRejectClick = { issue ->
            viewModel.rejectIssue(issue.id)
        },
        onDeleteClick = { issue ->
            viewModel.deleteIssue(issue.id)
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentManageIssuesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerView.adapter = issuesAdapter
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshIssues()
            }

            filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                when (checkedIds.firstOrNull()) {
                    R.id.pendingChip -> viewModel.filterIssues(IssueStatus.PENDING)
                    R.id.approvedChip -> viewModel.filterIssues(IssueStatus.APPROVED)
                    R.id.rejectedChip -> viewModel.filterIssues(IssueStatus.REJECTED)
                    else -> viewModel.filterIssues(null)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.issues.observe(viewLifecycleOwner) { issues ->
            issuesAdapter.submitList(issues)
            binding.emptyState.visibility = 
                if (issues.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }
} 