package com.example.civicconnect.ui.representatives

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.civicconnect.databinding.FragmentRepresentativeDetailsBinding
import com.example.civicconnect.ui.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepresentativeDetailsFragment : BaseFragment<FragmentRepresentativeDetailsBinding>() {

    private val viewModel: RepresentativeDetailsViewModel by viewModels()
    private val args: RepresentativeDetailsFragmentArgs by navArgs()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRepresentativeDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupViews()
        observeViewModel()
        viewModel.loadRepresentative(args.representativeId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViews() {
        with(binding) {
            emailButton.setOnClickListener {
                viewModel.representative.value?.email?.let { email ->
                    startEmailIntent(email)
                }
            }

            phoneButton.setOnClickListener {
                viewModel.representative.value?.phone?.let { phone ->
                    startPhoneIntent(phone)
                }
            }

            websiteButton.setOnClickListener {
                viewModel.representative.value?.website?.let { url ->
                    startWebsiteIntent(url)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.representative.observe(viewLifecycleOwner) { representative ->
            with(binding) {
                nameText.text = representative.name
                roleText.text = representative.role
                partyText.text = representative.party
                districtText.text = representative.district

                Glide.with(photoImage)
                    .load(representative.photoUrl)
                    .circleCrop()
                    .into(photoImage)

                emailButton.isEnabled = !representative.email.isNullOrBlank()
                phoneButton.isEnabled = !representative.phone.isNullOrBlank()
                websiteButton.isEnabled = !representative.website.isNullOrBlank()
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

    private fun startEmailIntent(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        startIntent(intent)
    }

    private fun startPhoneIntent(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        startIntent(intent)
    }

    private fun startWebsiteIntent(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startIntent(intent)
    }

    private fun startIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            showError("No app found to handle this action")
        }
    }
} 