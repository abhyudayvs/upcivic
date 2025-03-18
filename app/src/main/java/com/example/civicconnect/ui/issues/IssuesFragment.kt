package com.example.civicconnect.ui.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentIssuesBinding
import com.example.civicconnect.ui.adapter.IssuesAdapter
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IssuesFragment : BaseFragment<FragmentIssuesBinding>() {

    private val viewModel: IssuesViewModel by viewModels()
    private val issuesAdapter = IssuesAdapter(
        onIssueClick = { issue ->
            findNavController().navigate(
                IssuesFragmentDirections.actionIssuesFragmentToIssueDetailsFragment(issue.id)
            )
        },
        onSupportClick = { issue ->
            viewModel.toggleSupport(issue.id)
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentIssuesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            recyclerView.adapter = issuesAdapter
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshIssues()
            }
            fab.setOnClickListener {
                findNavController().navigate(
                    IssuesFragmentDirections.actionIssuesFragmentToCreateIssueFragment()
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.issues.observe(viewLifecycleOwner) { issues ->
            issuesAdapter.submitList(issues)
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