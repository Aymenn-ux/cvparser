// In CVParser.kt
private val specialtyKeywords = mapOf(
    "Software Development" to listOf("developer", "programming", "java", "kotlin", "android"),
    "Data Science" to listOf("data science", "machine learning", "ai", "python", "pandas"),
    // Add more specialties
)

fun classifySpecialty(text: String): String {
    val lowerText = text.lowercase()
    return specialtyKeywords.entries.firstOrNull { (_, keywords) ->
        keywords.any { lowerText.contains(it) }
    }?.key ?: "General"
}