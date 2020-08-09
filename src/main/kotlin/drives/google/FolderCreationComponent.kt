package drives.google

import GDFile
import com.google.api.services.drive.model.File

object FolderCreationComponent : GoogleDriveComponent() {

    fun save(folderName: String,
             parentId: String? = null): File
    {
        val fileMetadata = GDFile()
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