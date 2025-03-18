package com.example.civicconnect.ui.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentCreateIssueBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateIssueFragment : BaseFragment<FragmentCreateIssueBinding>() {

    private val viewModel: CreateIssueViewModel by viewModels()
    
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.setSelectedImage(it) }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateIssueBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupViews()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViews() {
        with(binding) {
            addImageButton.setOnClickListener {
                getContent.launch("image/*")
            }

            submitButton.setOnClickListener {
                val title = titleInput.editText?.text.toString()
                val description = descriptionInput.editText?.text.toString()
                val category = categoryInput.editText?.text.toString()
                val location = locationInput.editText?.text.toString()

                viewModel.createIssue(title, description, category, location)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.selectedImage.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.imagePreview.setImageURI(it)
                binding.imagePreview.visibility = View.VISIBLE
                binding.addImageButton.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.submitButton.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }

        viewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                findNavController().navigateUp()
            }
        }
    }
} 