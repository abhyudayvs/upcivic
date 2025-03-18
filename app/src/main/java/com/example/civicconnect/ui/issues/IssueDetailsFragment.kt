package com.example.civicconnect.ui.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.civicconnect.databinding.FragmentIssueDetailsBinding
import com.example.civicconnect.viewmodel.IssueViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.example.civicconnect.ui.base.BaseFragment
import com.example.civicconnect.util.formatDate

@AndroidEntryPoint
class IssueDetailsFragment : BaseFragment<FragmentIssueDetailsBinding>() {

    private val viewModel: IssueDetailsViewModel by viewModels()
    private val args: IssueDetailsFragmentArgs by navArgs()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentIssueDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupViews()
        observeViewModel()
        viewModel.loadIssue(args.issueId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViews() {
        with(binding) {
            supportButton.setOnClickListener {
                viewModel.toggleSupport()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.issue.observe(viewLifecycleOwner) { issue ->
            with(binding) {
                titleText.text = issue.title
                descriptionText.text = issue.description
                categoryText.text = issue.category
                locationText.text = issue.location
                dateText.text = formatDate(issue.createdAt)
                supportCountText.text = "${issue.supportCount} supporters"
                supportButton.isChecked = issue.isSupported(issue.userId)

                issue.photoUrl?.let { url ->
                    Glide.with(imageViewIssue)
                        .load(url)
                        .centerCrop()
                        .into(imageViewIssue)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 