package com.example.civicconnect.ui.admin.polls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentManagePollsBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagePollsFragment : BaseFragment<FragmentManagePollsBinding>() {

    private val viewModel: ManagePollsViewModel by viewModels()
    private val pollsAdapter = ManagePollsAdapter(
        onPollClick = { poll ->
            findNavController().navigate(
                ManagePollsFragmentDirections.actionManagePollsToPollDetails(poll.id)
            )
        },
        onActivateClick = { poll ->
            viewModel.activatePoll(poll.id)
        },
        onCloseClick = { poll ->
            viewModel.closePoll(poll.id)
        },
        onDeleteClick = { poll ->
            viewModel.deletePoll(poll.id)
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentManagePollsBinding.inflate(inflater, container, false)

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

            recyclerView.adapter = pollsAdapter
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshPolls()
            }

            filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                when (checkedIds.firstOrNull()) {
                    R.id.draftChip -> viewModel.filterPolls(PollStatus.DRAFT)
                    R.id.activeChip -> viewModel.filterPolls(PollStatus.ACTIVE)
                    R.id.closedChip -> viewModel.filterPolls(PollStatus.CLOSED)
                    else -> viewModel.filterPolls(null)
                }
            }

            createPollButton.setOnClickListener {
                findNavController().navigate(
                    ManagePollsFragmentDirections.actionManagePollsToCreatePoll()
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.polls.observe(viewLifecycleOwner) { polls ->
            pollsAdapter.submitList(polls)
            binding.emptyState.visibility = 
                if (polls.isEmpty()) View.VISIBLE else View.GONE
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