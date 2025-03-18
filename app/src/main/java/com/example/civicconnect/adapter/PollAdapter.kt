package com.example.civicconnect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.civicconnect.databinding.ItemPollBinding
import com.example.civicconnect.model.Poll
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class PollAdapter(
    private val onPollClick: (Poll) -> Unit
) : ListAdapter<Poll, PollAdapter.PollViewHolder>(PollDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val binding = ItemPollBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PollViewHolder(binding, onPollClick)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PollViewHolder(
        private val binding: ItemPollBinding,
        private val onPollClick: (Poll) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(poll: Poll) {
            binding.apply {
                textViewQuestion.text = poll.question
                textViewDescription.text = poll.description

                poll.endDate?.let { endDate ->
                    val timeUntilEnd = endDate - System.currentTimeMillis()
                    val daysUntilEnd = TimeUnit.MILLISECONDS.toDays(timeUntilEnd)
                    textViewEndDate.text = when {
                        daysUntilEnd > 1 -> "Ends in $daysUntilEnd days"
                        daysUntilEnd == 1L -> "Ends tomorrow"
                        daysUntilEnd == 0L -> "Ends today"
                        else -> "Ended"
                    }
                }

                root.setOnClickListener { onPollClick(poll) }
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