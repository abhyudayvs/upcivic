package com.example.civicconnect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.civicconnect.databinding.ItemRepresentativeBinding
import com.example.civicconnect.model.Representative

class RepresentativeAdapter(
    private val onRepresentativeClick: (Representative) -> Unit
) : ListAdapter<Representative, RepresentativeAdapter.RepresentativeViewHolder>(RepresentativeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        val binding = ItemRepresentativeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepresentativeViewHolder(binding, onRepresentativeClick)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RepresentativeViewHolder(
        private val binding: ItemRepresentativeBinding,
        private val onRepresentativeClick: (Representative) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(representative: Representative) {
            binding.apply {
                textViewName.text = representative.name
                textViewPosition.text = representative.position
                textViewParty.text = representative.party

                representative.photoUrl?.let { url ->
                    Glide.with(imageViewRepresentative)
                        .load(url)
                        .circleCrop()
                        .into(imageViewRepresentative)
                }

                root.setOnClickListener { onRepresentativeClick(representative) }
            }
        }
    }

    private class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem == newItem
        }
    }
} 