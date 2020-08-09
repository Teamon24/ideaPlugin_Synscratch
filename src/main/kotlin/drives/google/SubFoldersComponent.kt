package drives.google

import GDFile
import utils.GoogleDriveUtils

object SubFoldersComponent {

    fun getSubFolders(googleFolderIdParent: String?): List<GDFile> {
        val api = GoogleDriveUtils.createApi()
        var pageToken: String? = null
        val list = ArrayList<GDFile>()
        val query = "mimeType = '$VND_GOOGLE_APPS_FOLDER' and '$googleFolderIdParent' in parents"
        do {
            val result = api
                    .files()
                    .list()
                    .setQ(query)
                    .setSpaces("drive") // Fields will be assigned values: id, name, createdTime
                    .setFields("nextPageToken, files(id, name, createdTime)") //
                    .setPageToken(pageToken).execute()

            for (file in result.files) {
                list.add(file)
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)
        return list
    }

    fun main(args: Array<String>) {
        getSubFolders(null).forEach {
            println("Folder ID: ${it.id} --- Name: ${it.name}");
        }
    }
}