package com.example.cvparser.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cvparser.R
import com.example.cvparser.models.CVResult

class ResultAdapter(private val results: List<CVResult>) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvSpecialty: TextView = itemView.findViewById(R.id.tvSpecialty)
        val tvFilename: TextView = itemView.findViewById(R.id.tvFilename)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = results[position]

        holder.tvName.text = "👤 ${result.fullName}"
        holder.tvSpecialty.text = "💼 Specialty: ${result.specialty}"
        holder.tvFilename.text = "📄 ${result.cvFilename}"
    }

    override fun getItemCount(): Int = results.size
}