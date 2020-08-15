package services.google

import GoogleFile
import JFile
import dtos.FileDto
import dtos.FileDtoExtender
import enums.UnitType
import utils.getLastDir
import utils.splitTrim

internal object ServiceSupport {

    fun getFilesInDir(
        filesOfOneWordDirs: List<FileDtoExtender>,
        dirDtoExtender: FileDtoExtender
    ): List<FileDtoExtender> {
        return filesOfOneWordDirs.filter {
            it.relativePath.splitTrim("/")[0] == dirDtoExtender.relativePath.removePrefix("/")
        }
    }

    fun dropDirPrefix(
        infosOfNonOneWordDir: List<FileDtoExtender>,
        dirDtoExtender: FileDtoExtender
    ): List<FileDtoExtender> {
        return infosOfNonOneWordDir.map {
            FileDtoExtender(
                it.relativePath.removePrefix(dirDtoExtender.relativePath),
                it.file
            )
        }
    }

    fun uploadFile(filesLocalStringPath: String, fileDtoExtender: FileDtoExtender, id: String): GoogleFile {
        val filePathOnHost = filesLocalStringPath.removeSuffix("/") + fileDtoExtender.relativePath
        val file: JFile = JFile(filePathOnHost)
        return FileUploadComponent.uploadFile(
            id,
            contentType = null,
            customFileName = fileDtoExtender.file.name,
            uploadFile = file
        )
    }

    fun excludeSavedFiles(
        filesDtoExtenders: List<FileDtoExtender>,
        dirDtoExtender: FileDtoExtender,
        absPathsOfSimpleDirsFiles: List<String>
    ): List<FileDtoExtender> {
        return filesDtoExtenders.filter {
            it.relativePath != dirDtoExtender.relativePath &&
                    it.relativePath.splitTrim("/")[0] == dirDtoExtender.file.name &&
                    it.file.path !in absPathsOfSimpleDirsFiles
        }
    }

    fun getFilesInOneWordDirs(
        filesDtoExtenders: List<FileDtoExtender>,
        oneWordDirs: List<FileDtoExtender>
    ): List<FileDtoExtender> {
        val relativePathsOfOneWordDirs = oneWordDirs.map { it.relativePath.removePrefix("/") }
        return filesDtoExtenders
            .filter { fileInfoExtender ->
                val hasFile = fileInfoExtender.file.isFile()
                val split = fileInfoExtender.relativePath.splitTrim("/")
                val hasTwoParts = split.size == 2
                val dirName = split[0]
                hasFile && hasTwoParts && dirName in relativePathsOfOneWordDirs
            }
    }

    fun getOneWordDirs(filesDtoExtenders: List<FileDtoExtender>): List<FileDtoExtender> {
        val oneWordDirs = filesDtoExtenders.filter { it.hasOneLevelRelPath() && it.file.isDirectory() }
        return oneWordDirs
    }

    fun createRootDir(rootDirPath: String): FileDtoExtender {
        val relativePath = rootDirPath.getLastDir()
        val lastGoogleDir = relativePath
            .splitTrim("/")
            .fold(null as GoogleFile?) { parent, dir ->
                FolderCreationComponent.create(dir, parent?.id)
            }

        return FileDtoExtender(relativePath, FileDto(UnitType.DIRECTORY, rootDirPath), lastGoogleDir!!.id, lastGoogleDir.md5Checksum)
    }

    fun downloadFiles(fileDtoExtender: FileDtoExtender, targetFileDto: FileDto) {
        fileDtoExtender.googleId
    }

}
