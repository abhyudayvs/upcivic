package com.example.civicconnect.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.civicconnect.databinding.ItemPollBinding
import com.example.civicconnect.databinding.ItemPollOptionBinding
import com.example.civicconnect.model.Poll
import com.example.civicconnect.ui.adapter.base.BaseAdapter

class PollsAdapter(
    private val onPollClick: (Poll) -> Unit
) : BaseAdapter<Poll, ItemPollBinding>(PollDiffCallback()) {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean
    ): ItemPollBinding {
        return ItemPollBinding.inflate(inflater, parent, attachToParent)
    }

    override fun bind(binding: ItemPollBinding, item: Poll) {
        with(binding) {
            questionText.text = item.question
            descriptionText.text = item.description
            endDateText.text = item.getFormattedEndDate()
            totalVotesText.text = "${item.votes.size} votes"

            // Clear previous options
            optionsContainer.removeAllViews()

            // Add options dynamically
            item.options.forEachIndexed { index, option ->
                val optionView = ItemPollOptionBinding.inflate(
                    LayoutInflater.from(root.context),
                    optionsContainer,
                    false
                )

                with(optionView) {
                    optionText.text = option
                    val percentage = item.getVotePercentage(index)
                    progressIndicator.progress = percentage
                    percentageText.text = "$percentage%"
                }

                optionsContainer.addView(optionView.root)
            }

            root.setOnClickListener { onPollClick(item) }
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