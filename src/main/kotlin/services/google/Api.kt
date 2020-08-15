package services.google

import com.google.api.services.drive.Drive
import utils.GoogleDriveUtils



object Api {

    private val instance = GoogleDriveUtils.createApi()

    fun getInstance(): Drive {
        return instance;
    }
}