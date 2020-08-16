package utils

import dtos.FileDto
import enums.UnitType
import java.nio.file.Files
import java.nio.file.Path


object FileDtoUtils {
    fun getFileDtos(sourceStringPath: String, filesPaths: List<Path>): List<FileDto> {
        return filesPaths
            .map {
                val path = it.toAbsolutePath().toString().substringAfter(sourceStringPath.dropLastDir())
                val type = getType(it)
                FileDto(type, path)
            }
            .filter { it.path.isNotBlank() }
            .toList()
    }


    private fun getType(it: Path) = if (Files.isRegularFile(it)) UnitType.FILE else UnitType.DIRECTORY
}