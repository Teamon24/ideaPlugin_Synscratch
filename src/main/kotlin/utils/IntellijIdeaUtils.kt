package utils

import com.intellij.openapi.application.ApplicationInfo

object IntellijIdeaUtils {
    fun getFullVersion() = ApplicationInfo.getInstance().fullVersion
}