import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object ScratchesComponent {

    fun getScratchesSourcesPaths() =
        PluginConfigStorage
            .getAllIntelliJIdeaStringPaths()
            .dropLastWhile { it == "/" }

    fun doScratchesExist(pathToScratches: Path): Boolean {
        return Files.exists(pathToScratches)
    }

    fun areScratchesDirectory(pathToScratches: Path): Boolean {
        return Files.isDirectory(pathToScratches)
    }

    fun getScratches(stringPathToScratches: String): ArrayList<String> {
        val files = ArrayList<String>()
        Files.walk(Paths.get(stringPathToScratches)).forEach { files.add(it.toFile().absoluteFile.toString()) }
        return files
    }
}
