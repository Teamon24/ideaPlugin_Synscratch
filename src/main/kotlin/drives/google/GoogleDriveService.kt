package drives.google

import JFile
import drives.DriveService
import dropUserHomePart
import intellijIdeaRegex2
import utils.FileInfo

object GoogleDriveService: DriveService {

    override fun save(scratchStringPathsAndItFiles: Map<String, List<FileInfo>>) {
        scratchStringPathsAndItFiles.forEach { (scratchStringPath, files) ->
            println("source: $scratchStringPath")
            findSimpleDirAndSaveItsFiles(files, createRootDir(scratchStringPath), filesLocalStringPath = scratchStringPath.dropLast(1))
        }
    }

    private fun findSimpleDirAndSaveItsFiles(filesInfos: List<FileInfo>, lastDirId: String?, filesLocalStringPath: String) {
        FolderCreationComponent.api.files().emptyTrash().execute()

        val simpleDirs = filesInfos
            .filter { it.isSimpleDir() }
            .map { it.path.removePrefix("/") }

        val filesOfSimpleDirs = filesInfos
            .filter { fileInfo ->
                val split = fileInfo.path.split("/").filter { it.isNotBlank() }
                fileInfo.isFile() &&
                split.size == 2 &&
                split[0] in simpleDirs
            }

        val simpleDirsAndIds = simpleDirs.map {
            val savedDir = FolderCreationComponent.save(it, parentId = lastDirId)
            //EMIT THAT DIRECTORY WAS SAVED
            println("DIRECTORY WAS SAVED: $it")
            it to savedDir.id
        }.toMap()

        simpleDirsAndIds.map { (dir, id) ->
            val contentType: String? = null
            filesOfSimpleDirs.filter { it.isFile() && it.path.startsWith("/$dir") }.forEach {
                val fileLocalPath = filesLocalStringPath.removeSuffix("/") + it.path
                val file: JFile = JFile(fileLocalPath)
                FileCreationComponent.createFile(id, contentType, it.name, file)
                //EMIT THAT FILE WAS SAVED
                println("FILE WAS SAVED: ${it.name}")
            }
        }

        val absPathsOfSimpleDirsFiles = filesOfSimpleDirs.map { it.path }

        simpleDirsAndIds.forEach { (dir, id) ->
            val filesInfosOfCurrentDir = filesInfos.filter {
                        it.path != "/$dir" &&
                        it.path.split("/").filter { part -> part.isNotBlank() }[0] == dir &&
                        it.path !in absPathsOfSimpleDirsFiles
            }
            if (filesInfosOfCurrentDir.isNotEmpty()) {
                val droppedDirPrefix = filesInfosOfCurrentDir.map { FileInfo(it.type, it.path.removePrefix("/$dir"))}
                findSimpleDirAndSaveItsFiles(
                    droppedDirPrefix, id, "$filesLocalStringPath/$dir"
                )
            }

        }
    }

    private fun createRootDir(rootDirectory: String): String? {
        val lastDirId = dropUserHomePart(rootDirectory, intellijIdeaRegex2)
            .split("/").filter { it.isNotBlank() }
            .fold(null as String?) { parentId, dir ->
                FolderCreationComponent.save(dir, parentId).id
            }

        return lastDirId
    }


}

