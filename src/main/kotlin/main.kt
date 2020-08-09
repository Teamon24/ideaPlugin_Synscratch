import drives.google.GoogleDriveService
import utils.FileInfo
import utils.UnitType
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Matcher
import java.util.regex.Pattern

const val APPLICATION_NAME = "Save Scratches"

const val postfix = "IntelliJIdea"
const val dir = ".$postfix"

val intellijIdeaRegex2 = Regex("\\.${postfix}[\\d]{4}\\.[\\d][^.]*")

const val SPLIT_REGEX = "scratches"


fun main() {

    val scratchesSourcesPaths = ScratchesComponent.getScratchesSourcesPaths()

    val scratchPathsAndItFiles =
        scratchesSourcesPaths
            .map { pathToScratches ->
                val filesStringPaths = ScratchesComponent.getScratches(pathToScratches)
                val pathFromIntelliJIdeaDir = dropUserHomePart(pathToScratches, intellijIdeaRegex2)
                pathToScratches to getFileInfos(filesStringPaths)
            }.toMap()

    GoogleDriveService.save(scratchPathsAndItFiles)

}


fun dropUserHomePart(pathWithIntelliJIdeaMention: String, regex: Regex): String {
    val pattern: Pattern = Pattern.compile(regex.pattern)
    val matcher: Matcher = pattern.matcher(pathWithIntelliJIdeaMention)
    while (matcher.find()) {
        return matcher.group().toString()
    }

    throw RuntimeException("In path '$pathWithIntelliJIdeaMention' was not found presence of string '$postfix'")
}

private fun getPathRelativeToScratches (filesStringPaths: ArrayList<String>): List<String> {
    return filesStringPaths
        .asSequence()
        .map { it.split(SPLIT_REGEX)[1] }
        .filter { it.isNotBlank() }
        .toList()
}

private fun getFileInfos(filesStringPaths: ArrayList<String>): List<FileInfo> {
    return filesStringPaths
        .map { FileInfo(getType(it), it.split(SPLIT_REGEX)[1]) }
        .filter { it.path.isNotBlank() }
        .toList()
}

private fun getType(it: String) = if(isDirectory(it)) UnitType.DIRECTORY else UnitType.FILE
private fun isDirectory(it: String) = Files.isDirectory(Paths.get(it))
