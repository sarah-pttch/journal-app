package com.study.journalapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.study.journalapp.databinding.ItemImagePageBinding
import java.io.File

class ImagePagerAdapter(private val paths: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemImagePageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemImagePageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = paths.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.photo.load(File(paths[position]))
    }
}