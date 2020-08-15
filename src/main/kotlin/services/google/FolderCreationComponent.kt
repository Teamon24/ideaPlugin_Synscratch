package services.google

import GoogleFile
import utils.GoogleDriveUtils.VND_GOOGLE_APPS_FOLDER

object FolderCreationComponent : GoogleDriveComponent() {

    fun create(folderName: String,
               parentId: String? = null): GoogleFile
    {
        val fileMetadata = GoogleFile()
        fileMetadata.name = folderName
        fileMetadata.mimeType = VND_GOOGLE_APPS_FOLDER
        parentId?.let {
            if (fileMetadata.parents == null) {
                fileMetadata.parents = ArrayList<String>()
            }
            fileMetadata.parents.add(it)
        }

        val savedDir = api.files().create(fileMetadata)
            .setFields("id")
            .execute()

        return savedDir
    }

}