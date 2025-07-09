package com.example.cvparser.utils

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.File

class PDFParser(private val context: Context) {
    init {
        PDFBoxResourceLoader.init(context)
    }

    fun extractTextFromPDF(file: File): String {
        return try {
            val document = PDDocument.load(file)
            val stripper = PDFTextStripper()
            stripper.text
        } catch (e: Exception) {
            ""
        }
    }

    fun extractNameFromText(text: String, filename: String): String {
        // Implement your name extraction logic here
        // This is a simplified version
        val namePattern = Regex("""(?i)\b([A-Z][a-z]+(?:\s+[A-Z][a-z]+)+\b""")
        return namePattern.find(text)?.value ?: filename.substringBeforeLast(".").replace("_", " ")
            }
        }


    fun classifySpecialty(text: String): String {
        // Implement your specialty classification logic here
        // This is a simplified version
        val keywords = mapOf(
            "developer" to "Software Development",
            "engineer" to "Engineering",
            "design" to "Design",
            "manager" to "Management"
        )

        for ((key, value) in keywords) {
            if (text.lowercase().contains(key)) {
                return value
            }
        }
        return "General"
    }
}