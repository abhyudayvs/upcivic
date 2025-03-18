package com.example.civicconnect.ui.admin.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.civicconnect.databinding.ItemManageUserBinding
import com.example.civicconnect.model.User
import com.example.civicconnect.model.UserStatus

class ManageUsersAdapter(
    private val onUserClick: (User) -> Unit,
    private val onSuspendClick: (User) -> Unit,
    private val onBanClick: (User) -> Unit,
    private val onActivateClick: (User) -> Unit
) : ListAdapter<User, ManageUsersAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemManageUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(
        private val binding: ItemManageUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                nameText.text = user.name
                emailText.text = user.email
                statusChip.text = user.status.name
                joinDateText.text = user.getFormattedJoinDate()
                activityText.text = "${user.issueCount} issues · ${user.pollCount} polls"

                Glide.with(avatarImage)
                    .load(user.photoUrl)
                    .circleCrop()
                    .into(avatarImage)

                root.setOnClickListener { onUserClick(user) }
                suspendButton.setOnClickListener { onSuspendClick(user) }
                banButton.setOnClickListener { onBanClick(user) }
                activateButton.setOnClickListener { onActivateClick(user) }

                // Update button states based on user status
                when (user.status) {
                    UserStatus.ACTIVE -> {
                        suspendButton.isEnabled = true
                        banButton.isEnabled = true
                        activateButton.isEnabled = false
                    }
                    UserStatus.SUSPENDED -> {
                        suspendButton.isEnabled = false
                        banButton.isEnabled = true
                        activateButton.isEnabled = true
                    }
                    UserStatus.BANNED -> {
                        suspendButton.isEnabled = false
                        banButton.isEnabled = false
                        activateButton.isEnabled = true
                    }
                }
            }
        }
    }

    private class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
} 