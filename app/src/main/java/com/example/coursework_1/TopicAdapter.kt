package com.example.coursework_1

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TopicAdapter(private val items: List<String>,
    private var selectedItem: String?,
    private var isSelectable: Boolean) : RecyclerView.Adapter<TopicAdapter.StringViewHolder>() {

    private var previousText: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return StringViewHolder(view)
    }

    override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
        val item = items[position]
        holder.topicText.text = item
        if (item == selectedItem) {
            holder.topicText.setTypeface(null, Typeface.BOLD)
            holder.topicText.setBackgroundColor(Color.CYAN)
            previousText = holder.topicText
        }
    }

    override fun getItemCount(): Int = items.size

    inner class StringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var topicText = itemView.findViewById<View>(R.id.topicRecyclerViewText) as TextView
        init {
            itemView.setOnClickListener {
                if (isSelectable) {
                    topicText.setTypeface(null, Typeface.BOLD)
                    topicText.setBackgroundColor(Color.CYAN)
                    previousText?.setTypeface(null, Typeface.NORMAL)
                    previousText?.setBackgroundColor(Color.WHITE)
                    previousText = topicText
                    val database = UserInformation.getDatabase()
                    val userID = UserInformation.getCurrentUserID()
                    val ref = database.getReference(userID.toString())
                    ref.updateChildren(mapOf("topic" to topicText.text.toString()))

                    UserInformation.setDatabase("topic", topicText.text.toString())
                }
            }
        }
    }
}
