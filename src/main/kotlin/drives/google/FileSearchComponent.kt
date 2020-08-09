package drives.google

import GDFile
import utils.GoogleDriveUtils
import com.google.api.services.drive.model.File

object FileSearchComponent : GoogleDriveComponent() {

    fun getFilesByName(fileNameLike: String): List<File> {
        var pageToken: String? = null
        val list = ArrayList<GDFile>()
        val query = (" name contains '$fileNameLike' and mimeType != '$VND_GOOGLE_APPS_FOLDER' ")
        do {
            val result = api
                .files()
                .list()
                .setQ(query)
                .setSpaces("drive") // Fields will be assigned values: id, name, createdTime, mimeType
                .setFields("nextPageToken, files(id, name, createdTime, mimeType)")
                .setPageToken(pageToken).execute()
            for (file in result.files) {
                list.add(file)
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)

        return list
    }

    fun main(args: Array<String>) {
        getFilesByName("u").forEach { println("Mime Type: ${it.mimeType} --- Name: ${it.name}") }
    }
}