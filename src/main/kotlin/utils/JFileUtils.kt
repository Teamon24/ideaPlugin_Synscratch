package utils

import java.nio.file.Files
import java.nio.file.Paths

object JFileUtils {

    /**
     * @return
     * false - if all sub directories was present and so was not created,
     * true - if at least one of sub directories was absent and so was created.
     */
    fun makeDirs(root: JFile, subdirsNames: List<String>): Boolean {
        var allSubdirsIsCreated = true

        subdirsNames.fold(root) { prevJDir, subdirName ->
            val jFile = JFile(prevJDir, subdirName)
            val notExists = Files.notExists(Paths.get(subdirName))
            allSubdirsIsCreated = notExists && allSubdirsIsCreated
            if (notExists) {
                jFile.mkdir()
            }
            jFile
        }

        return allSubdirsIsCreated
    }
}