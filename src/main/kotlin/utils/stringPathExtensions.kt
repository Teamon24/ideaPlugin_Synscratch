package utils

fun String.dropSourcePart(sourceStringPath: String): String {
    return this.replaceFirst(sourceStringPath, "")
}

fun String.dropLastDir(): String {
    return this.dropLastWhile { it == '/' }.substringBeforeLast('/')
}

fun String.getLastDir(): String {
    return this.substringAfterLast("/").also {
        if (it.isBlank()) return this.substringAfterLast("/")
    }
}

fun String.splitTrim(delimiter: String): List<String> {
    return this.split(delimiter).filter { part -> part.isNotBlank() }
}