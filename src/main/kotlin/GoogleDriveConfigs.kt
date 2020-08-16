import pojos.UploadDownloadMapping
import pojos.UserConfigsPojo
import services.google.FolderCreationComponent
import services.google.GoogleDriveSearchComponent
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

object GoogleDriveConfigs: DriveConfigs() {

    val drivePluginStashName = getDrivesPluginStashName()
    var drivesPluginStashId: String? = null
    const val jsonConfigsFileName = "drive.google.json"
    var userConfigs: UserConfigsPojo? = null

    fun initDrivesPluginStash(): Boolean {
        val dirs = GoogleDriveSearchComponent.getDirs(drivePluginStashName)
        val stashIsAbsent = dirs.isEmpty() || dirs.first().name != drivePluginStashName
        drivesPluginStashId = if (stashIsAbsent) {
            val googleFile = FolderCreationComponent.create(drivePluginStashName, null)
            googleFile.id
        } else {
            dirs.first().id
        }
        return stashIsAbsent
    }

    private fun getDrivesPluginStashName(): String {
        return "DRIVES_PLUGIN_STASH"
    }

    fun initUserConfigs(): Boolean {
        val jsonConfigStringPath = "$userConfigDir/$jsonConfigsFileName"
        val jsonConfigPath = Paths.get(jsonConfigStringPath)
        return if (Files.exists(jsonConfigPath)) {
            val fileUrl = URL("file:$jsonConfigStringPath")
            this.userConfigs = this.objectMapper.readValue(fileUrl, UserConfigsPojo::class.java)
            false
        } else {
            Files.createFile(jsonConfigPath)
            jsonConfigPath.toFile().writeText("""{"user":"empty_one", "drivesPluginDirName": "DRIVES_PLUGIN_DEFAULT_NAME", "uploadDownloadMapping": []}""")
            true
        }
    }

    fun getUploadDownloadMappings(): List<UploadDownloadMapping> {
        return this.userConfigs!!.uploadDownloadMapping
    }
}