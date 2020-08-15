package services.google

import GoogleFile
import com.google.api.services.drive.model.FileList
import utils.GoogleDriveUtils
import utils.GoogleDriveUtils.VND_GOOGLE_APPS_FOLDER

object SearchComponent : GoogleDriveComponent() {

    fun getFiles(fileName: String): List<GoogleFile> {
        var pageToken: String? = null
        val list = ArrayList<GoogleFile>()
        val query = (" name = '$fileName' and mimeType != '$VND_GOOGLE_APPS_FOLDER' and trashed = false")
        do {
            val result = execute(query, pageToken)
            for (file in result.files) {
                list.add(file)
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)

        return list
    }

    fun getDirs(dirName: String): List<GoogleFile> {
        var pageToken: String? = null
        val list = ArrayList<GoogleFile>()
        val query = (" name = '$dirName' and mimeType = '$VND_GOOGLE_APPS_FOLDER' and trashed = false")
        do {
            val result = execute(query, pageToken)
            for (file in result.files) {
                list.add(file)
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)

        return list
    }

    fun getSubDir(googleFolderIdParent: String?): List<GoogleFile> {
        val api = GoogleDriveUtils.createApi()
        var pageToken: String? = null
        val list = ArrayList<GoogleFile>()
        val query = "mimeType = '$VND_GOOGLE_APPS_FOLDER' and '$googleFolderIdParent' in parents and trashed = false"
        do {
            val result = execute(query, pageToken)

            for (file in result.files) {
                list.add(file)
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)
        return list
    }

    private fun execute(query: String, pageToken: String?): FileList {
        return api
            .files()
            .list()
            .setQ(query)
            .setSpaces("")
            .setFields("nextPageToken, files(id, name)") //
            .setPageToken(pageToken)
            .execute()
    }

}