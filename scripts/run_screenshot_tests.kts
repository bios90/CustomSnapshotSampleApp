import java.io.File

val rootDirPath = File("").absolutePath
val rootDir = File(rootDirPath)
val allureMergedResultsDirectory = File(rootDir, "build/allure_merged_results")

fun foreachallureResultDirectory(action: (File) -> Unit) {
    val rootDirFiles = rootDir.listFiles()
    for (childFile in rootDirFiles) {
        if (childFile.isDirectory) {
            val allureResultDirectory = childFile.listFiles().find { it.name == "allure-results" }
            if (allureResultDirectory != null) {
                action.invoke(allureResultDirectory)
            }
        }
    }
}

fun exec(command: String): String =
    Runtime.getRuntime().exec(command).inputStream.bufferedReader().use { it.readText() }.trimEnd()

allureMergedResultsDirectory.deleteRecursively()
foreachallureResultDirectory { allureResultDirectory -> allureResultDirectory.deleteRecursively() }
exec("./gradlew clean test -i")
allureMergedResultsDirectory.mkdir()
foreachallureResultDirectory { allureResultDirectory ->
    for (file in allureResultDirectory.listFiles()) {
        val copyFile = File(allureMergedResultsDirectory, file.name)
        file.copyTo(copyFile)
    }
}
exec("allure serve ${allureMergedResultsDirectory.absolutePath}")
