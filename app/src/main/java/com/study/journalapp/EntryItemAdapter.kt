package com.study.journalapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.study.journalapp.data.Entry

class EntryItemAdapter(val clickListener: (entryId: Long) -> Unit)
    : RecyclerView.Adapter<EntryItemAdapter.EntryItemViewHolder>() {
    var data = listOf<Entry>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryItemViewHolder =
        EntryItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: EntryItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    class EntryItemViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
        val entryTitle = rootView.findViewById<TextView>(R.id.entry_title)
        val entryDate = rootView.findViewById<TextView>(R.id.entry_date)
        val entryContent = rootView.findViewById<TextView>(R.id.entry_content_preview)
        companion object {
            fun inflateFrom(parent: ViewGroup): EntryItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.entry_item, parent, false) as CardView
                return EntryItemViewHolder(view)
            }
        }

        fun bind(item: Entry, clickListener: (entryId: Long) -> Unit) {
            entryTitle.text = item.title
            entryDate.text = item.creationDate.toString()
            entryContent.text = item.content
            rootView.setOnClickListener { clickListener(item.entryId) }
        }
    }
}