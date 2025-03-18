package com.example.civicconnect.ui.polls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentCreatePollBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePollFragment : BaseFragment<FragmentCreatePollBinding>() {

    private val viewModel: CreatePollViewModel by viewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreatePollBinding.inflate(inflater, container, false)

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
            addOptionButton.setOnClickListener {
                val optionCount = optionsContainer.childCount
                if (optionCount < MAX_OPTIONS) {
                    val optionInput = layoutInflater.inflate(
                        R.layout.item_poll_option_input,
                        optionsContainer,
                        false
                    )
                    optionsContainer.addView(optionInput)
                }
            }

            submitButton.setOnClickListener {
                val question = questionInput.editText?.text.toString()
                val description = descriptionInput.editText?.text.toString()
                val endDate = endDateInput.editText?.text.toString()
                val options = getOptions()

                viewModel.createPoll(question, description, endDate, options)
            }
        }
    }

    private fun getOptions(): List<String> {
        val options = mutableListOf<String>()
        with(binding) {
            for (i in 0 until optionsContainer.childCount) {
                val optionView = optionsContainer.getChildAt(i)
                val optionInput = optionView.findViewById<TextInputEditText>(R.id.optionInput)
                val option = optionInput.text.toString()
                if (option.isNotBlank()) {
                    options.add(option)
                }
            }
        }
        return options
    }

    private fun observeViewModel() {
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

    companion object {
        private const val MAX_OPTIONS = 5
    }
} 