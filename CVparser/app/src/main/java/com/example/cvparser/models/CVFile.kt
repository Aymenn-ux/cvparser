package com.example.cvparser.models

data class CVFile(
    val id: String,
    val name: String,
    val size: Long,
    val path: String,
    var status: FileStatus = FileStatus.PENDING
)

enum class FileStatus {
    PENDING, PROCESSING, SUCCESS, ERROR
}