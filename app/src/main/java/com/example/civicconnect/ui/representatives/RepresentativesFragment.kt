package com.example.civicconnect.ui.representatives

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.civicconnect.adapter.RepresentativeAdapter
import com.example.civicconnect.databinding.FragmentRepresentativesBinding
import com.example.civicconnect.viewmodel.RepresentativeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.example.civicconnect.ui.adapter.RepresentativesAdapter
import com.example.civicconnect.ui.base.BaseFragment

@AndroidEntryPoint
class RepresentativesFragment : BaseFragment<FragmentRepresentativesBinding>() {

    private val viewModel: RepresentativesViewModel by viewModels()
    private val representativesAdapter = RepresentativesAdapter(
        onRepresentativeClick = { representative ->
            findNavController().navigate(
                RepresentativesFragmentDirections
                    .actionRepresentativesFragmentToRepresentativeDetailsFragment(representative.id)
            )
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRepresentativesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            recyclerView.adapter = representativesAdapter
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshRepresentatives()
            }
            searchInput.setEndIconOnClickListener {
                val address = searchInput.editText?.text.toString()
                if (address.isNotBlank()) {
                    viewModel.searchByAddress(address)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.representatives.observe(viewLifecycleOwner) { representatives ->
            representativesAdapter.submitList(representatives)
            binding.emptyState.visibility = 
                if (representatives.isEmpty()) View.VISIBLE else View.GONE
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