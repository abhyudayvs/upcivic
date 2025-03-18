package com.example.civicconnect.ui.polls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentPollsBinding
import com.example.civicconnect.ui.adapter.PollsAdapter
import com.example.civicconnect.ui.base.BaseFragment
import com.example.civicconnect.viewmodel.PollViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PollsFragment : BaseFragment<FragmentPollsBinding>() {

    private val viewModel: PollViewModel by viewModels()
    private val pollsAdapter = PollsAdapter(
        onPollClick = { poll ->
            findNavController().navigate(
                PollsFragmentDirections.actionPollsFragmentToPollDetailsFragment(poll.id)
            )
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPollsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            recyclerView.adapter = pollsAdapter
            swipeRefresh.setOnRefreshListener {
                viewModel.loadActivePolls()
            }
            fab.setOnClickListener {
                findNavController().navigate(
                    PollsFragmentDirections.actionPollsFragmentToCreatePollFragment()
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.polls.observe(viewLifecycleOwner) { polls ->
            pollsAdapter.submitList(polls)
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