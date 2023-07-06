package com.example.listmaker.interfaces

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.databinding.LandmarkListLandmarkBinding
import com.example.listmaker.models.Landmark

class LandmarkListAdapter(private val onItemClicked: (Landmark) -> Unit) :
    ListAdapter<Landmark, LandmarkListAdapter.LandmarkViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandmarkViewHolder {
        return LandmarkViewHolder(
            LandmarkListLandmarkBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: LandmarkViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class LandmarkViewHolder(private var binding: LandmarkListLandmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Landmark) {
            binding.landmarkName.text = item.landmarkName
            binding.landmarkDescription.text = item.landmarkDescription
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Landmark>() {
            override fun areItemsTheSame(oldItem: Landmark, newItem: Landmark): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Landmark, newItem: Landmark): Boolean {
                return oldItem.landmarkName == newItem.landmarkName
            }
        }
    }
}