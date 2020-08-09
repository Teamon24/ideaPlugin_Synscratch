package drives.google

import com.google.api.services.drive.Drive
import utils.GoogleDriveUtils

/**
 *
 */

const val VND_GOOGLE_APPS_FOLDER = "application/vnd.google-apps.folder"

object Api {

    private val instance = GoogleDriveUtils.createApi()

    fun getInstance(): Drive {
        return instance;
    }
}