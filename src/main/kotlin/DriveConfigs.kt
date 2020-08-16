import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import utils.JFile
import utils.JFileUtils

abstract class DriveConfigs {

    companion object {
        const val drivePluginUserDir = ".DrivesPlugin2020.01"
        val USER_HOME = getUserHome()

        private fun getUserHome() = "/home/artem"
    }

    fun initDrivesPluginUserDir() = JFileUtils.makeDirs(JFile(USER_HOME), listOf(drivePluginUserDir, "config"))

    val userDir: String


    internal val userConfigDir: String
    internal val objectMapper: ObjectMapper

    init {
        this.objectMapper = kotlinObjectMapper()
        this.userDir = "${USER_HOME}/${drivePluginUserDir}"
        this.userConfigDir = "${USER_HOME}/${drivePluginUserDir}/config"
    }


    fun kotlinObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        return mapper
    }
}


