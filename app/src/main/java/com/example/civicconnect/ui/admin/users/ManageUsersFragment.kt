package com.example.civicconnect.ui.admin.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civicconnect.databinding.FragmentManageUsersBinding
import com.example.civicconnect.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageUsersFragment : BaseFragment<FragmentManageUsersBinding>() {

    private val viewModel: ManageUsersViewModel by viewModels()
    private val usersAdapter = ManageUsersAdapter(
        onUserClick = { user ->
            findNavController().navigate(
                ManageUsersFragmentDirections.actionManageUsersToUserDetails(user.id)
            )
        },
        onSuspendClick = { user ->
            viewModel.suspendUser(user.id)
        },
        onBanClick = { user ->
            viewModel.banUser(user.id)
        },
        onActivateClick = { user ->
            viewModel.activateUser(user.id)
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentManageUsersBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerView.adapter = usersAdapter
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshUsers()
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.searchUsers(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchUsers(newText)
                    return true
                }
            })

            filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                when (checkedIds.firstOrNull()) {
                    R.id.activeChip -> viewModel.filterUsers(UserStatus.ACTIVE)
                    R.id.suspendedChip -> viewModel.filterUsers(UserStatus.SUSPENDED)
                    R.id.bannedChip -> viewModel.filterUsers(UserStatus.BANNED)
                    else -> viewModel.filterUsers(null)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            usersAdapter.submitList(users)
            binding.emptyState.visibility = 
                if (users.isEmpty()) View.VISIBLE else View.GONE
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