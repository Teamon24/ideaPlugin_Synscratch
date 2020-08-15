package services

import dtos.FileDto
import dtos.FileDtoExtender
import kotlinx.coroutines.flow.Flow

interface DriveFlow {
    fun getUploadFlow(scratchStringPathsAndItFiles: Map<String, List<FileDto>>): Flow<FileDtoExtender>
    fun getDowloadFlow(sourceDtoExtender: FileDtoExtender, targetFileDto: FileDto): Flow<FileDtoExtender>
}