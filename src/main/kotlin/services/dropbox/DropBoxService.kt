package services.dropbox

import services.DriveService
import dtos.FileDto
import java.nio.file.Path

object DropBoxService: DriveService {

    override fun upload(scratchStringPathsAndItFiles: Map<String, List<FileDto>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFreeSpace(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isUploadable(firstDirToUpload: List<Path>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}