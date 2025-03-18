package com.example.civicconnect.ui.polls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.civicconnect.databinding.FragmentPollDetailsBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PollDetailsFragment : BaseFragment<FragmentPollDetailsBinding>() {

    private val viewModel: PollDetailsViewModel by viewModels()
    private val args: PollDetailsFragmentArgs by navArgs()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPollDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupViews()
        observeViewModel()
        viewModel.loadPoll(args.pollId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViews() {
        with(binding) {
            optionsGroup.setOnCheckedChangeListener { _, checkedId ->
                viewModel.vote(checkedId)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.poll.observe(viewLifecycleOwner) { poll ->
            with(binding) {
                questionText.text = poll.question
                descriptionText.text = poll.description
                endDateText.text = poll.getFormattedEndDate()
                totalVotesText.text = "${poll.votes.size} votes"

                // Clear previous options
                optionsContainer.removeAllViews()

                // Add options dynamically
                poll.options.forEachIndexed { index, option ->
                    val optionView = layoutInflater.inflate(
                        R.layout.item_poll_option,
                        optionsContainer,
                        false
                    )
                    
                    with(optionView) {
                        optionText.text = option
                        val percentage = poll.getVotePercentage(index)
                        progressIndicator.progress = percentage
                        percentageText.text = "$percentage%"
                    }

                    optionsContainer.addView(optionView)
                }
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