package utils

import APPLICATION_NAME
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.common.collect.Lists
import java.io.*


object GoogleDriveUtils {

    val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private const val tokensDirectoryPath = "tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    val SCOPES: MutableSet<String> = DriveScopes.all()

    const val CREDENTIALS_FILE_PATH = "/credentials.json"

    /**
     * Creates an authorized Credential object.
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    fun getCredentials(httpTransport: NetHttpTransport): Credential { // Load client secrets.
        val clientSecrets = getClientSecrets()
        // Build flow and trigger user authorization request.
        val flow = getFlow(httpTransport, clientSecrets)
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("teamon24")
    }

    fun getClientSecrets(): GoogleClientSecrets {
        val inputStream =
            GoogleDriveUtils::class.java.getResourceAsStream(
                CREDENTIALS_FILE_PATH
            ) ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")

        return GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))
    }

    private fun getFlow(
        httpTransport: NetHttpTransport,
        clientSecrets: GoogleClientSecrets
    ): GoogleAuthorizationCodeFlow? {
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            JSON_FACTORY, clientSecrets,
            SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType("offline")
            .build()
        return flow
    }

    fun setUpCredentials(httpTransport: HttpTransport): GoogleCredential {
        val jacksonFactory = JacksonFactory.getDefaultInstance()
        // Go to the Google API Console, open your application's
        // credentials page, and copy the client ID and client secret.
        // Then paste them into the following code.
        val redirectUrl = "urn:ietf:wg:oauth:2.0:oob"
        val scope = "https://www.googleapis.com/auth/content"
        val clientSecrets = getClientSecrets()
        val authorizationFlow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jacksonFactory,
            clientSecrets,
            Lists.newArrayList(scope)
        )
            .setAccessType("offline").build()
        val authorizeUrl = authorizationFlow.newAuthorizationUrl().setRedirectUri(redirectUrl).build()
        println("Paste this url in your browser: \n$authorizeUrl\n")
        // Wait for the authorization code.
        println("Type the code you received here: ")
        val authorizationCode = BufferedReader(InputStreamReader(System.`in`)).readLine()
        // Authorize the OAuth2 token.
        val tokenRequest = authorizationFlow.newTokenRequest(authorizationCode)
        tokenRequest.redirectUri = redirectUrl
        val tokenResponse = tokenRequest.execute()
        // Create the OAuth2 credential.
        val credential = GoogleCredential.Builder()
            .setTransport(NetHttpTransport())
            .setJsonFactory(JacksonFactory())
            .setClientSecrets(clientSecrets)
            .build()
        // Set authorized credentials.
        credential.setFromTokenResponse(tokenResponse)
        return credential
    }

    fun createApi(): Drive {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val credentials = getCredentials(httpTransport)
        return createApi(httpTransport, credentials)
    }

    fun createApiWithCode(): Drive {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val credentials = setUpCredentials(httpTransport)
        return createApi(httpTransport, credentials)
    }

    fun createApi(httpTransport: HttpTransport, credentials: Credential): Drive {
        return Drive.Builder(httpTransport, JSON_FACTORY, credentials)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }
}