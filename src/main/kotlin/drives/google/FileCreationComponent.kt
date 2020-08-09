package drives.google

import JFile
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.model.File
import java.io.IOException
import java.io.InputStream

object FileCreationComponent : GoogleDriveComponent() {

    @Throws(IOException::class)
    fun createFile(
        googleFolderIdParent: String?,
        contentType: String?,
        customFileName: String,
        uploadData: ByteArray?
    ): File {
        val uploadStreamContent: AbstractInputStreamContent = ByteArrayContent(contentType, uploadData)

        return createFile(
            googleFolderIdParent,
            contentType,
            customFileName,
            uploadStreamContent
        )
    }

    @Throws(IOException::class)
    fun createFile(
        googleFolderIdParent: String?,
        contentType: String?,
        customFileName: String,
        uploadFile: JFile
    ): File { //
        val uploadStreamContent: AbstractInputStreamContent = FileContent(contentType, uploadFile)
        return createFile(
            googleFolderIdParent,
            contentType,
            customFileName,
            uploadStreamContent
        )
    }

    @Throws(IOException::class)
    fun createFile(
        googleFolderIdParent: String?,
        contentType: String?,
        customFileName: String,
        inputStream: InputStream?
    ): File {
        val uploadStreamContent: AbstractInputStreamContent = InputStreamContent(contentType, inputStream)
        return createFile(
            googleFolderIdParent,
            contentType,
            customFileName,
            uploadStreamContent
        )
    }

    private fun createFile(
        googleFolderIdParent: String?,
        contentType: String?,  //
        customFileName: String,
        uploadStreamContent: AbstractInputStreamContent
    ): File {

        val fileMetadata = File()
        fileMetadata.name = customFileName
        fileMetadata.parents = listOf(googleFolderIdParent)
        fileMetadata.mimeType = contentType

        return api
            .files()
            .create(fileMetadata, uploadStreamContent)
            .setFields("id, webContentLink, webViewLink, parents")
            .execute()
    }

    fun main(args: Array<String>) {

        val uploadFile = java.io.File("/home/tran/Downloads/test.txt")
        val googleFile = createFile(
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