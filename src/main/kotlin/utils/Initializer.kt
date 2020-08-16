package utils

import GoogleDriveConfigs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object Initializer {
    fun getInitializationFlow(): Flow<String> {
        return flow {

            GoogleDriveConfigs.initDrivesPluginUserDir().also {
                if (it) {
                    emit(""""${GoogleDriveConfigs.userConfigDir}" folder was created in ${GoogleDriveConfigs.userDir}""")
                } else {
                    emit(""""${GoogleDriveConfigs.userConfigDir}" folder has already been created in ${GoogleDriveConfigs.userDir}""")
                }
            }

            GoogleDriveConfigs.initUserConfigs().also {
                if (it) {
                    emit(""""${GoogleDriveConfigs.jsonConfigsFileName}" file was created in ${GoogleDriveConfigs.userConfigDir}""")
                } else {
                    emit(""""${GoogleDriveConfigs.jsonConfigsFileName}" has already been created in ${GoogleDriveConfigs.userConfigDir}""")
                }
            }

            GoogleDriveConfigs.initDrivesPluginStash().also {
                if (it) {
                    emit(""""${GoogleDriveConfigs.drivePluginStashName}" folder was created at Google Drive""")
                } else {
                    emit(""""${GoogleDriveConfigs.drivePluginStashName}" folder has already been created at Google Drive.""")
                }
            }
        }

    }
}