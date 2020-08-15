package dtos

import enums.UnitType

data class FileDto(val type: UnitType, val path: String) {

    val name: String

    fun isFile() = type == UnitType.FILE
    fun isDirectory() = type == UnitType.DIRECTORY

    init {
        val split = path.split("/")
        this.name = split.last()
    }
}
