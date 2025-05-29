// File: adapter/DuaAdapter.kt
package com.drmiaji.hisnulmuslim.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.drmiaji.hisnulmuslim.R
import com.drmiaji.hisnulmuslim.data.entities.DuaName
import com.drmiaji.hisnulmuslim.utils.DuaDiffCallback

class DuaAdapter(
    private var duaNames: List<DuaName>,
    private val onItemClick: (DuaName) -> Unit
) : RecyclerView.Adapter<DuaAdapter.DuaViewHolder>() {

    private var currentQuery: String = ""
    private var bengaliTypeface: Typeface? = null

    inner class DuaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val duaNameText: TextView = itemView.findViewById(R.id.dua_name)
        private val chapterNameText: TextView = itemView.findViewById(R.id.chapter_name)
        private val duaNumberText: TextView = itemView.findViewById(R.id.dua_number)

        fun bind(duaName: DuaName) {
            // Set Bengali/custom typeface if available
            bengaliTypeface?.let { typeface ->
                duaNameText.typeface = typeface
                chapterNameText.typeface = typeface
            }

            // Set chapter name (chapname) with search highlighting
            duaNameText.text = highlightSearchQuery(duaName.chapname.orEmpty(), currentQuery)

            // Set category name with search highlighting
            chapterNameText.text = highlightSearchQuery(duaName.category.orEmpty(), currentQuery)

            // Display chapter ID
            duaNumberText.text = duaName.chap_id.toString()

            // Handle item click
            itemView.setOnClickListener { onItemClick(duaName) }
        }

        private fun highlightSearchQuery(text: String, query: String): SpannableString {
            val spannableString = SpannableString(text)
            if (query.isNotEmpty()) {
                val startIndex = text.indexOf(query, ignoreCase = true)
                if (startIndex >= 0) {
                    val endIndex = startIndex + query.length
                    val highlightColor = ContextCompat.getColor(itemView.context, R.color.search_highlight)
                    spannableString.setSpan(
                        BackgroundColorSpan(highlightColor),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return spannableString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuaViewHolder {
        // Load custom font once (move to init if you want to avoid loading in every onCreateViewHolder)
        if (bengaliTypeface == null) {
            try {
                bengaliTypeface = Typeface.createFromAsset(parent.context.assets, "fonts/solaimanlipi.ttf")
            } catch (e: Exception) {
                // Optionally log or handle font errors
            }
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dua_name, parent, false)
        return DuaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DuaViewHolder, position: Int) {
        holder.bind(duaNames[position])
    }

    override fun getItemCount(): Int = duaNames.size

    fun updateData(newDuaNames: List<DuaName>, searchQuery: String) {
        val diffCallback = DuaDiffCallback(this.duaNames, newDuaNames)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.duaNames = newDuaNames
        this.currentQuery = searchQuery
        diffResult.dispatchUpdatesTo(this)
    }
}