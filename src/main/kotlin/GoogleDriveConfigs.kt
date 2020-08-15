import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import pojos.DriveConfigsPojo
import pojos.UploadDownloadMapping
import services.google.FolderCreationComponent
import services.google.SearchComponent
import java.net.URL

object GoogleDriveConfigs {


    const val drivePluginUserDir = ".DrivesPlugin2020.01"
    val configs: DriveConfigsPojo

    val userHome: String
    private val drivePluginStashName: String
    private val objectMapper: ObjectMapper
    private var drivePluginStashId: String? = null

    init {
        userHome = getUserHomeFromConfigs()
        objectMapper = kotlinObjectMapper()
        drivePluginStashName = getDrivesPluginDirFromConfigs()


        val fileUrl = URL("file:${this.userHome}/$drivePluginUserDir/config/drive.google.json")
        configs = this.objectMapper.readValue(fileUrl, DriveConfigsPojo::class.java)
    }

    fun initDrivesPluginStash() {
        val dirs = SearchComponent.getDirs(this.drivePluginStashName)
        if (dirs.isEmpty() || dirs.first().name != this.drivePluginStashName) {
            val googleFile = FolderCreationComponent.create(this.drivePluginStashName, null)
            drivePluginStashId = googleFile.id
            println("${this.drivePluginStashName} folder was create.")
        } else {
            drivePluginStashId = dirs.first().id
            println("${this.drivePluginStashName} folder has already been created.")
        }
    }

    fun drivesPluginStashId() = this.drivePluginStashId!!

    fun kotlinObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        //mapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE;
        //mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
        //mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        return mapper
    }

    fun getUploadDownloadMappings(): List<UploadDownloadMapping> {
        return this.configs.uploadDownloadMapping
    }

    private fun getUserHomeFromConfigs(): String {
        return "/home/artem"
    }

    private fun getDrivesPluginDirFromConfigs(): String {
        return "DRIVES_PLUGIN_STASH"
    }
}