/**
 *
 */
object PluginConfigStorage {
    private const val PATH_TO_SCRATCHES_CONFIGS = "config/$SPLIT_REGEX/"
    private val USER_HOME = "/home/artem"

    fun getAllIntelliJIdeaStringPaths() = arrayListOf(
        "$USER_HOME/.IntelliJIdea2019.3/$PATH_TO_SCRATCHES_CONFIGS"
        //, "$USER_HOME/.IntelliJIdea2077.7/$PATH_TO_SCRATCHES_CONFIGS"
    )
}