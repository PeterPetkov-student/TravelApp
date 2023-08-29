package com.example.listmaker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.databinding.CityListCityBinding
import com.example.listmaker.models.City

class CityListAdapter(private val onItemClicked: (City) -> Unit,
                      private val onItemLongClick: (City) -> Unit) :
    ListAdapter<City, CityListAdapter.CityViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(
            CityListCityBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(current)
            true
        }
        holder.bind(current)
    }

    class CityViewHolder(private var binding: CityListCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: City) {
            binding.cityName.text = item.cityName
            binding.cityDescription.text = item.cityDescription
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<City>() {
            override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.cityName == newItem.cityName
            }
        }
    }
}