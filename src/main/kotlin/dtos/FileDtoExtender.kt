package dtos

import utils.splitTrim

data class FileDtoExtender(val relativePath: String, val file: FileDto) {

    var md5Checksum: String? = null
    var googleId: String? = null

    constructor(
        relativePath: String,
        file: FileDto,
        googleId: String?) : this(relativePath, file)
    {
        this.googleId = googleId
    }

    constructor(
        relativePath: String,
        file: FileDto,
        googleId: String?,
        md5Checksum: String) : this(relativePath, file, googleId)
    {
        this.md5Checksum = md5Checksum
    }

    private val hasOneLevelRelPath = relativePath.splitTrim("/").size == 1

    fun hasOneLevelRelPath(): Boolean {
        return this.hasOneLevelRelPath
    }
}