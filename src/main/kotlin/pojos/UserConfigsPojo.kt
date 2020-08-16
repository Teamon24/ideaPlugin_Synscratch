package pojos

data class UserConfigsPojo(
    val user: String,
    val drivesPluginDirName: String,
    val drivesPluginDirId: String?,
    val uploadDownloadMapping: List<UploadDownloadMapping> = ArrayList()
)

data class UploadDownloadMapping(
    val uploadFrom: String,
    val downloadTo: String?
)