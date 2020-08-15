package services.google

import dtos.FileDto
import dtos.FileDtoExtender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import services.DriveFlow
import utils.dropLastDir

object GoogleDriveFlow : DriveFlow {

    override fun getUploadFlow(sourcesPathsAndFiles: Map<String, List<FileDto>>): Flow<FileDtoExtender> {
        return kotlinx.coroutines.flow.flow {
            sourcesPathsAndFiles.map { (scratchStringPath, files) ->
                println("source: $scratchStringPath")
                val fileInfoExtender = files.map { FileDtoExtender(it.path, it) }
                val filesLocalStringPath = scratchStringPath.dropLastDir()
                findSimpleDirAndSaveItsFiles(
                    fileInfoExtender,
                    GoogleDriveConfigs.drivesPluginStashId(),
                    filesLocalStringPath = filesLocalStringPath
                )
            }
        }
    }

    override fun getDowloadFlow(sourceDtoExtender: FileDtoExtender, targetFileDto: FileDto): Flow<FileDtoExtender> {
        return kotlinx.coroutines.flow.flow { ServiceSupport.downloadFiles(sourceDtoExtender, targetFileDto) }
    }

    private suspend fun FlowCollector<FileDtoExtender>.findSimpleDirAndSaveItsFiles(
        filesDtoExtenders: List<FileDtoExtender>,
        lastDirId: String?,
        filesLocalStringPath: String
    ) {
        val oneWordDirs = ServiceSupport.getOneWordDirs(filesDtoExtenders)
        val oneWordDirsAndIds = oneWordDirs.map {
            val savedDir = FolderCreationComponent.create(it.relativePath.removePrefix("/"), parentId = lastDirId)
            it.googleId = savedDir.id
            emit(it)
            it to savedDir.id
        }.toMap()

        val filesOfOneWordDirs = ServiceSupport.getFilesInOneWordDirs(filesDtoExtenders, oneWordDirs)

        oneWordDirsAndIds.map { (dirInfoExtender, id) ->
            ServiceSupport.getFilesInDir(filesOfOneWordDirs, dirInfoExtender).forEach {
                val uploadedFile = ServiceSupport.uploadFile(filesLocalStringPath, it, id)
                it.googleId = uploadedFile.id
                it.md5Checksum = uploadedFile.md5Checksum
                emit(it)
            }
        }

        val absPathsOfSimpleDirsFiles = filesOfOneWordDirs.map { it.relativePath }

        oneWordDirsAndIds.forEach { (dirInfoExtender, id) ->
            val infosOfNonOneWordDir = ServiceSupport.excludeSavedFiles(filesDtoExtenders, dirInfoExtender, absPathsOfSimpleDirsFiles)
            if (infosOfNonOneWordDir.isNotEmpty()) {
                val droppedDirPrefix = ServiceSupport.dropDirPrefix(infosOfNonOneWordDir, dirInfoExtender)
                findSimpleDirAndSaveItsFiles(
                    droppedDirPrefix, id, "$filesLocalStringPath${dirInfoExtender.relativePath}"
                )
            }
        }
    }
}
