package com.litedroidstudios.livongosteps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SummaryAdapter : RecyclerView.Adapter<SummaryViewHolder>() {
    private val summaries = mutableListOf<StepSummary>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val li = LayoutInflater.from(parent.context)
        return SummaryViewHolder(li.inflate(R.layout.item_summary, parent, false))
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val summary = summaries[position]
        holder.dateTextView?.text = summary.getDateFormatted()
        holder.stepsTextView?.text = summary.steps.toString()
    }

    override fun getItemCount(): Int {
        return summaries.size
    }

    fun setList(summaryList: List<StepSummary>?) {
        this.summaries.clear()

        summaryList?.let {
            this.summaries.addAll(summaryList!!)
        }

        notifyDataSetChanged()
    }
}

class SummaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var dateTextView: TextView? = view.findViewById(R.id.date_textview)
    var stepsTextView: TextView? = view.findViewById(R.id.steps_textview)
}