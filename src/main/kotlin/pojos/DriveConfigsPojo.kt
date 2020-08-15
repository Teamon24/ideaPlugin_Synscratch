package pojos

data class DriveConfigsPojo(
    val user: String,
    val drivesPluginDirName: String,
    val drivesPluginDirId: String?,
    val uploadDownloadMapping: List<UploadDownloadMapping> = ArrayList()
)

data class UploadDownloadMapping(
    val upload: String,
    val download: String?
)