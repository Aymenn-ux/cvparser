package com.example.cvparser.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cvparser.R
import com.example.cvparser.models.CVFile
import com.example.cvparser.models.FileStatus
import com.example.cvparser.utils.FileUtils

class FileAdapter(
    private val files: MutableList<CVFile>,
    private val onRemoveClick: (CVFile) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]

        holder.tvFileName.text = file.name
        holder.tvFileSize.text = FileUtils.getFileSize(file.size)

        when (file.status) {
            FileStatus.PENDING -> {
                holder.tvStatus.text = "Pending"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
            }
            FileStatus.PROCESSING -> {
                holder.tvStatus.text = "Processing"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_processing)
            }
            FileStatus.SUCCESS -> {
                holder.tvStatus.text = "Success"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_success)
            }
            FileStatus.ERROR -> {
                holder.tvStatus.text = "Error"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_error)
            }
        }

        holder.btnRemove.setOnClickListener {
            onRemoveClick(file)
        }
    }

    override fun getItemCount(): Int = files.size

    fun updateFiles(newFiles: List<CVFile>) {
        files.clear()
        files.addAll(newFiles)
        notifyDataSetChanged()
    }
}