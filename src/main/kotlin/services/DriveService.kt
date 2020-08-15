package services

import dtos.FileDto
import java.nio.file.Path

interface DriveService {
    fun upload(scratchStringPathsAndItFiles: Map<String, List<FileDto>>)
    fun getFreeSpace(): Long
    fun isUploadable(toUpload: List<Path>): Boolean
}

