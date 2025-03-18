package com.example.civicconnect.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentDashboardBinding
import com.example.civicconnect.utils.safeNavigate
import com.example.civicconnect.viewmodel.AuthViewModel
import com.example.civicconnect.viewmodel.IssueViewModel
import com.example.civicconnect.viewmodel.PollViewModel

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val authViewModel: AuthViewModel by viewModels()
    private val issueViewModel: IssueViewModel by viewModels()
    private val pollViewModel: PollViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        loadDashboardData()
    }

    private fun setupObservers() {
        issueViewModel.issues.observe(viewLifecycleOwner) { issues ->
            // Update recent issues UI
        }

        pollViewModel.activePolls.observe(viewLifecycleOwner) { polls ->
            // Update active polls UI
        }
    }

    private fun navigateToIssueDetails(issueId: String) {
        val action = DashboardFragmentDirections.actionDashboardToIssueDetails(issueId)
        findNavController().safeNavigate(action)
    }

    private fun navigateToPollDetails(pollId: String) {
        val action = DashboardFragmentDirections.actionDashboardToPollDetails(pollId)
        findNavController().safeNavigate(action)
    }

    private fun loadDashboardData() {
        issueViewModel.loadUserIssues()
        pollViewModel.loadActivePolls()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 