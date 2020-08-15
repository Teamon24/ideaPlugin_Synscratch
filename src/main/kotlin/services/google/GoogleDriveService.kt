package services.google

import dtos.FileDto
import dtos.FileDtoExtender
import services.DriveService
import java.nio.file.Files
import java.nio.file.Path

object GoogleDriveService: DriveService {

    override fun isUploadable(toUpload: List<Path>): Boolean {
        val uploadBodySize = toUpload.map { Files.size(it) }.sum()
        val freeDriveSpace = this.getFreeSpace()
        return uploadBodySize <= freeDriveSpace
    }

    override fun getFreeSpace(): Long {
        return FileUploadComponent.getFreeSpace()
    }

    override fun upload(scratchStringPathsAndItFiles: Map<String, List<FileDto>>) {
        scratchStringPathsAndItFiles.forEach { (sourceStringPath, files) ->
            println("source: $sourceStringPath")
            val fileInfoExtender = files.map { FileDtoExtender(it.path, it)}
            val rootDirDtoExtender = ServiceSupport.createRootDir(sourceStringPath)
            findSimpleDirAndSaveItsFiles(
                fileInfoExtender,
                rootDirDtoExtender.googleId,
                filesLocalStringPath = sourceStringPath.dropLast(1)
            )
        }
    }

    private fun findSimpleDirAndSaveItsFiles(filesDtoExtenders: List<FileDtoExtender>,
                                             lastDirId: String?,
                                             filesLocalStringPath: String)
    {
        FolderCreationComponent.api.files().emptyTrash().execute()

        val oneWordDirs = ServiceSupport.getOneWordDirs(filesDtoExtenders)
        val oneWordDirsAndIds = oneWordDirs.map {
            val savedDir = FolderCreationComponent.create(it.relativePath, parentId = lastDirId)
            println("DIRECTORY WAS SAVED: ${it.file.path}")
            it to savedDir.id
        }.toMap()

        val filesOfOneWordDirs = ServiceSupport.getFilesInOneWordDirs(filesDtoExtenders, oneWordDirs)

        oneWordDirsAndIds.map { (dirInfoExtender, id) ->
            ServiceSupport.getFilesInDir(filesOfOneWordDirs, dirInfoExtender).forEach {
                ServiceSupport.uploadFile(filesLocalStringPath, it, id)
                println("FILE WAS SAVED: ${it.file.path}")
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
