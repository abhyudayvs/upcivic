package com.example.civicconnect.ui.admin.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.civicconnect.databinding.ItemManageIssueBinding
import com.example.civicconnect.model.Issue

class ManageIssuesAdapter(
    private val onIssueClick: (Issue) -> Unit,
    private val onApproveClick: (Issue) -> Unit,
    private val onRejectClick: (Issue) -> Unit,
    private val onDeleteClick: (Issue) -> Unit
) : ListAdapter<Issue, ManageIssuesAdapter.IssueViewHolder>(IssueDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val binding = ItemManageIssueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IssueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IssueViewHolder(
        private val binding: ItemManageIssueBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(issue: Issue) {
            with(binding) {
                titleText.text = issue.title
                descriptionText.text = issue.description
                statusChip.text = issue.status.name
                dateText.text = issue.getFormattedDate()

                Glide.with(imageView)
                    .load(issue.photoUrl)
                    .centerCrop()
                    .into(imageView)

                root.setOnClickListener { onIssueClick(issue) }
                approveButton.setOnClickListener { onApproveClick(issue) }
                rejectButton.setOnClickListener { onRejectClick(issue) }
                deleteButton.setOnClickListener { onDeleteClick(issue) }

                // Update button states based on issue status
                when (issue.status) {
                    IssueStatus.PENDING -> {
                        approveButton.isEnabled = true
                        rejectButton.isEnabled = true
                    }
                    else -> {
                        approveButton.isEnabled = false
                        rejectButton.isEnabled = false
                    }
                }
            }
        }
    }

    private class IssueDiffCallback : DiffUtil.ItemCallback<Issue>() {
        override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem == newItem
        }
    }
} 