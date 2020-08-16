package services.google

import utils.GoogleFile
import utils.JFile
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import java.io.File
import java.io.IOException
import java.io.InputStream


object FileUploadComponent : GoogleDriveComponent() {

    @Throws(IOException::class)
    fun uploadFile(
        googleFolderIdParent: String?,
        contentType: String?,
        customFileName: String,
        uploadData: ByteArray?
    ): GoogleFile {
        val uploadStreamContent: AbstractInputStreamContent = ByteArrayContent(contentType, uploadData)

        return uploadFile(
            googleFolderIdParent,
            contentType,
            customFileName,
            uploadStreamContent
        )
    }

    @Throws(IOException::class)
    fun uploadFile(
        googleFolderIdParent: String?,
        contentType: String?,
        customFileName: String,
        uploadFile: JFile
    ): GoogleFile { //
        val uploadStreamContent: AbstractInputStreamContent = FileContent(contentType, uploadFile)
        return uploadFile(
            googleFolderIdParent,
            contentType,
            customFileName,
            uploadStreamContent
        )
    }

    @Throws(IOException::class)
    fun uploadFile(
        googleFolderIdParent: String?,
        contentType: String?,
        customFileName: String,
        inputStream: InputStream?
    ): GoogleFile {
        val uploadStreamContent: AbstractInputStreamContent = InputStreamContent(contentType, inputStream)
        return uploadFile(
            googleFolderIdParent,
            contentType,
            customFileName,
            uploadStreamContent
        )
    }

    fun getFreeSpace(): Long {
        val get = api.about().get()
        get.fields = "*"
        val storageQuota = get.execute().storageQuota
        return storageQuota.limit - storageQuota.usageInDrive
    }

    private fun updateFile(
        service: Drive, fileId: String, newTitle: String,
        newDescription: String, newMimeType: String, newFilename: String, newRevision: Boolean
    ): GoogleFile? {
        return try { // First retrieve the file from the API.
            val file = service.files().get(fileId).execute()
            file.description = newDescription
            file.mimeType = newMimeType
            // File's new content.
            val fileContent = File(newFilename)
            val mediaContent = FileContent(newMimeType, fileContent)
            // Send the request to the API.
            service.files().update(fileId, file, mediaContent).execute()
        } catch (e: IOException) {
            println("An error occurred: $e")
            null
        }
    }

    private fun uploadFile(
        googleFolderIdParent: String?,
        contentType: String?,  //
        customFileName: String,
        uploadStreamContent: AbstractInputStreamContent
    ): GoogleFile {

        val fileMetadata = GoogleFile()
        fileMetadata.name = customFileName
        fileMetadata.parents = listOf(googleFolderIdParent)
        fileMetadata.mimeType = contentType

        return api
            .files()
            .create(fileMetadata, uploadStreamContent)
            .setFields("id, webContentLink, webViewLink, parents, md5Checksum")
            .execute()
    }

    fun main(args: Array<String>) {

        val uploadFile = java.io.File("/home/tran/Downloads/test.txt")
        val googleFile = uploadFile(
            null,
            "text/plain",
            "newfile.txt",
            uploadFile
        )

        println("Created Google file!")
        println("WebContentLink: ${googleFile.webContentLink}")
        println("WebViewLink: ${googleFile.webViewLink}")
        println("Done!")
    }
}