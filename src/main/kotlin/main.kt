import dtos.FileDtoExtender
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import services.google.GoogleDriveFlow
import services.google.GoogleDriveService
import utils.FileDtoUtils
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList


fun main() {

    GoogleDriveConfigs.initDrivesPluginStash()

    val uploadDownloadMappings = GoogleDriveConfigs.getUploadDownloadMappings()

    val allUploadDirs =
        uploadDownloadMappings
            .map { it.upload }
            .dropLastWhile { it == "/" }

    val firstDirToUpload = allUploadDirs[0]
    val walk = Files.walk(Paths.get(firstDirToUpload)).toList()

    val uploadable = GoogleDriveService.isUploadable(walk)

    if (uploadable) {
        val pathAndItFiles = firstDirToUpload to FileDtoUtils.getFileDtos(firstDirToUpload, walk)
        val uploaded = ArrayList<FileDtoExtender>()
        val amount = pathAndItFiles.second.size
        val uploadFlow = GoogleDriveFlow.getUploadFlow(hashMapOf(pathAndItFiles))

        runBlocking {
            uploadFlow.collect {
                uploaded.add(it)
                println("[${uploaded.size}/$amount] ${it.file.type} was saved: ${it.file.path}")
            }
        }
    }
}


