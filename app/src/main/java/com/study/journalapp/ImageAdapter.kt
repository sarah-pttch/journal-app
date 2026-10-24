package com.study.journalapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.study.journalapp.databinding.ImageItemBinding
import java.io.File

class ImageAdapter(
    private val onRemove: ((String) -> Unit)? = null
) : ListAdapter<String, ImageAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(a: String, b: String) = a == b
    override fun areContentsTheSame(a: String, b: String) = a == b
}) {
    class ViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = getItem(position)
        holder.binding.image.load(File(path))
        holder.binding.removeButton.isVisible = onRemove != null
        if (onRemove != null) {
            holder.binding.removeButton.setOnClickListener { onRemove(path) }
        }
    }
}