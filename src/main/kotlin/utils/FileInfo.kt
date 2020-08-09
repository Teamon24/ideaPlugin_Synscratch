package utils

class FileInfo(val type: UnitType, var path: String) {

    private val simpleDir: Boolean
    val name: String

    fun isFile() = type == UnitType.FILE
    fun isDirectory() = type == UnitType.DIRECTORY
    fun isSimpleDir() = simpleDir

    init {
        val split = path.split("/")
        this.simpleDir = split.filter { it.isNotBlank() }.size == 1
        this.name = split.last()
    }
}
