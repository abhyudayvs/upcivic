package com.example.civicconnect.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.civicconnect.databinding.ItemIssueBinding
import com.example.civicconnect.model.Issue
import com.example.civicconnect.ui.adapter.base.BaseAdapter
import com.example.civicconnect.util.formatDate

class IssuesAdapter(
    private val onIssueClick: (Issue) -> Unit,
    private val onSupportClick: (Issue) -> Unit
) : BaseAdapter<Issue, ItemIssueBinding>(IssueDiffCallback()) {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean
    ): ItemIssueBinding {
        return ItemIssueBinding.inflate(inflater, parent, attachToParent)
    }

    override fun bind(binding: ItemIssueBinding, item: Issue) {
        with(binding) {
            titleText.text = item.title
            descriptionText.text = item.description
            categoryText.text = item.category
            locationText.text = item.location
            dateText.text = formatDate(item.createdAt)
            supportCountText.text = item.supportCount.toString()
            supportButton.isChecked = item.isSupported(item.userId)

            root.setOnClickListener { onIssueClick(item) }
            supportButton.setOnClickListener { onSupportClick(item) }
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