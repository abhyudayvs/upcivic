package com.example.civicconnect.ui.admin.polls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.civicconnect.databinding.ItemManagePollBinding
import com.example.civicconnect.model.Poll
import com.example.civicconnect.model.PollStatus

class ManagePollsAdapter(
    private val onPollClick: (Poll) -> Unit,
    private val onActivateClick: (Poll) -> Unit,
    private val onCloseClick: (Poll) -> Unit,
    private val onDeleteClick: (Poll) -> Unit
) : ListAdapter<Poll, ManagePollsAdapter.PollViewHolder>(PollDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val binding = ItemManagePollBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PollViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PollViewHolder(
        private val binding: ItemManagePollBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(poll: Poll) {
            with(binding) {
                titleText.text = poll.title
                descriptionText.text = poll.description
                statusChip.text = poll.status.name
                dateText.text = poll.getFormattedDate()
                votesText.text = "${poll.totalVotes} votes"

                root.setOnClickListener { onPollClick(poll) }
                activateButton.setOnClickListener { onActivateClick(poll) }
                closeButton.setOnClickListener { onCloseClick(poll) }
                deleteButton.setOnClickListener { onDeleteClick(poll) }

                // Update button states based on poll status
                when (poll.status) {
                    PollStatus.DRAFT -> {
                        activateButton.isEnabled = true
                        closeButton.isEnabled = false
                    }
                    PollStatus.ACTIVE -> {
                        activateButton.isEnabled = false
                        closeButton.isEnabled = true
                    }
                    PollStatus.CLOSED -> {
                        activateButton.isEnabled = false
                        closeButton.isEnabled = false
                    }
                }
            }
        }
    }

    private class PollDiffCallback : DiffUtil.ItemCallback<Poll>() {
        override fun areItemsTheSame(oldItem: Poll, newItem: Poll): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Poll, newItem: Poll): Boolean {
            return oldItem == newItem
        }
    }
} 