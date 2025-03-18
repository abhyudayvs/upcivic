package com.example.civicconnect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.civicconnect.databinding.ItemIssueBinding
import com.example.civicconnect.model.Issue

class IssueAdapter(
    private val onIssueClick: (Issue) -> Unit
) : ListAdapter<Issue, IssueAdapter.IssueViewHolder>(IssueDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val binding = ItemIssueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IssueViewHolder(binding, onIssueClick)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class IssueViewHolder(
        private val binding: ItemIssueBinding,
        private val onIssueClick: (Issue) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(issue: Issue) {
            binding.apply {
                textViewTitle.text = issue.title
                textViewCategory.text = issue.category
                textViewStatus.text = issue.status

                issue.photoUrl?.let { url ->
                    Glide.with(imageViewIssue)
                        .load(url)
                        .centerCrop()
                        .into(imageViewIssue)
                }

                root.setOnClickListener { onIssueClick(issue) }
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