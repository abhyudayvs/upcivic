package com.example.civicconnect.ui.issues

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.R
import com.example.civicconnect.databinding.FragmentReportIssueBinding
import com.example.civicconnect.viewmodel.IssueViewModel
import com.google.android.material.snackbar.Snackbar

class ReportIssueFragment : Fragment() {
    private var _binding: FragmentReportIssueBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: IssueViewModel by viewModels()
    private var selectedPhotoUri: Uri? = null
    
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            binding.imageViewPhoto.apply {
                visibility = View.VISIBLE
                setImageURI(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategorySpinner()
        setupClickListeners()
        setupObservers()
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Road", "Garbage", "Streetlight", "Park", "Other")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, categories)
        binding.spinnerCategory.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        binding.buttonAddPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.buttonSubmit.setOnClickListener {
            submitIssue()
        }
    }

    private fun setupObservers() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonSubmit.isEnabled = !isLoading
        }
    }

    private fun submitIssue() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()
        val category = binding.spinnerCategory.text.toString()

        if (title.isBlank() || description.isBlank() || category.isBlank()) {
            Snackbar.make(binding.root, "Please fill all required fields", Snackbar.LENGTH_LONG).show()
            return
        }

        viewModel.reportIssue(title, description, category, "", selectedPhotoUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 