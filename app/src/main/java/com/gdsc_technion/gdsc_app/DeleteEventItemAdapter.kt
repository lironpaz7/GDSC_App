package com.gdsc_technion.gdsc_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gdsc_technion.gdsc_app.databinding.ItemDeleteEventBinding

class DeleteEventItemAdapter(
    private val onClick: (View, EventData) -> Unit
) : ListAdapter<EventData, DeleteEventItemAdapter.EventDataViewHolder>(
    object : DiffUtil.ItemCallback<EventData>() {
        override fun areItemsTheSame(oldItem: EventData, newItem: EventData): Boolean =
            oldItem.timeObj == newItem.timeObj

        override fun areContentsTheSame(oldItem: EventData, newItem: EventData): Boolean =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDataViewHolder =
        EventDataViewHolder(
            ItemDeleteEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )

    override fun onBindViewHolder(holder: EventDataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventDataViewHolder(
        private val binding: ItemDeleteEventBinding,
        private val onClick: (View, EventData) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventData) {
            binding.root.setOnClickListener { onClick(binding.root, event) }
            binding.event = event
            binding.executePendingBindings()
        }
    }
}