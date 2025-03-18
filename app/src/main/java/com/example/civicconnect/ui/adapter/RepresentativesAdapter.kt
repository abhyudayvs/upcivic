package com.example.civicconnect.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.civicconnect.databinding.ItemRepresentativeBinding
import com.example.civicconnect.model.Representative

class RepresentativesAdapter(
    private val onRepresentativeClick: (Representative) -> Unit
) : ListAdapter<Representative, RepresentativesAdapter.RepresentativeViewHolder>(RepresentativeDiffCallback()) {

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
            with(binding) {
                nameText.text = representative.name
                roleText.text = representative.role
                partyChip.text = representative.party

                Glide.with(photoImage)
                    .load(representative.photoUrl)
                    .circleCrop()
                    .into(photoImage)

                root.setOnClickListener {
                    onRepresentativeClick(representative)
                }
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