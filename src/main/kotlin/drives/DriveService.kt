package drives

import utils.FileInfo

interface DriveService {
    fun save(scratchStringPathsAndItFiles: Map<String, List<FileInfo>>)
}