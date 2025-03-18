package com.example.civicconnect.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentSignUpBinding
import com.example.civicconnect.ui.base.BaseFragment
import com.example.civicconnect.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private val viewModel: AuthViewModel by viewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignUpBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            signUpButton.setOnClickListener {
                val name = nameInput.editText?.text.toString()
                val email = emailInput.editText?.text.toString()
                val password = passwordInput.editText?.text.toString()
                viewModel.signUp(name, email, password)
            }

            loginButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToIssuesFragment()
                )
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.signUpButton.isEnabled = !isLoading
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