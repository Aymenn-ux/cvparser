package com.example.cvparser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cvparser.adapters.FileAdapter
import com.example.cvparser.adapters.ResultAdapter
import com.example.cvparser.databinding.ActivityMainBinding
import com.example.cvparser.models.CVFile
import com.example.cvparser.models.CVResult
import com.example.cvparser.models.FileStatus
import com.example.cvparser.utils.FileUtils
import com.example.cvparser.utils.PDFParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val files = mutableListOf<CVFile>()
    private lateinit var fileAdapter: FileAdapter
    private val results = mutableListOf<CVResult>()
    private lateinit var resultAdapter: ResultAdapter
    private lateinit var pdfParser: PDFParser

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                val uris = intent.clipData?.let { clipData ->
                    List(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
                } ?: listOf(intent.data!!)

                uris.forEach { uri ->
                    val file = FileUtils.getFileFromUri(this, uri)
                    file?.let {
                        files.add(CVFile(
                            id = System.currentTimeMillis().toString(),
                            name = it.name,
                            size = it.length(),
                            path = it.absolutePath
                        ))
                    }
                }

                updateFileList()
                binding.btnProcess.isEnabled = files.isNotEmpty()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfParser = PDFParser(this)

        setupRecyclerViews()
        setupClickListeners()
    }

    private fun setupRecyclerViews() {
        fileAdapter = FileAdapter(files) { file ->
            files.remove(file)
            updateFileList()
            binding.btnProcess.isEnabled = files.isNotEmpty()
        }

        resultAdapter = ResultAdapter(results)

        binding.rvFiles.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileAdapter
        }

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = resultAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnSelectFiles.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            filePickerLauncher.launch(intent)
        }

        binding.btnProcess.setOnClickListener {
            processFiles()
        }

        binding.btnDownload.setOnClickListener {
            downloadResults()
        }
    }

    private fun updateFileList() {
        fileAdapter.updateFiles(files)
    }

    private fun processFiles() {
        binding.btnProcess.isEnabled = false
        files.forEach { it.status = FileStatus.PROCESSING }
        updateFileList()

        CoroutineScope(Dispatchers.IO).launch {
            val newResults = mutableListOf<CVResult>()

            files.forEach { file ->
                try {
                    val text = pdfParser.extractTextFromPDF(File(file.path))
                    if (text.isNotEmpty()) {
                        val name = pdfParser.extractNameFromText(text, file.name)
                        val specialty = pdfParser.classifySpecialty(text)

                        if (name.isNotEmpty()) {
                            newResults.add(CVResult(
                                cvFilename = file.name,
                                fullName = name,
                                specialty = specialty
                            ))
                            file.status = FileStatus.SUCCESS
                        } else {
                            file.status = FileStatus.ERROR
                        }
                    } else {
                        file.status = FileStatus.ERROR
                    }
                } catch (e: Exception) {
                    file.status = FileStatus.ERROR
                }
            }

            withContext(Dispatchers.Main) {
                results.clear()
                results.addAll(newResults)
                updateFileList()
                binding.btnProcess.isEnabled = true

                if (results.isNotEmpty()) {
                    resultAdapter.notifyDataSetChanged()
                    binding.tvResultsTitle.visibility = View.VISIBLE
                    binding.rvResults.visibility = View.VISIBLE
                    binding.btnDownload.visibility = View.VISIBLE
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No CVs were successfully processed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun downloadResults() {
        if (results.isEmpty()) return

        val csvContent = StringBuilder()
        csvContent.append("CV Filename,Full Name,Specialty\n")

        results.forEach { result ->
            csvContent.append("\"${result.cvFilename}\",\"${result.fullName}\",\"${result.specialty}\"\n")
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "cv_analysis_results.csv")
            putExtra(Intent.EXTRA_TEXT, csvContent.toString())
        }

        startActivity(intent)
    }
}