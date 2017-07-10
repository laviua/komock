package ua.com.lavi.komock.app

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import ua.com.lavi.komock.KomockRunner
import ua.com.lavi.komock.engine.model.config.KomockConfiguration
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by Oleksandr Loushkin
 */

object KomockApplication {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @JvmStatic fun main(args: Array<String>) {
        if (args.isNotEmpty()) {
            runApplication(args[0])
        } else {
            log.error("Please input path with the configuration! Example: komock-app mock_example.yml")
        }
    }

    private fun runApplication(path: String) {
        log.info("Run Komock version: ${version()}. config path: $path")
        try {
            Files.newInputStream(Paths.get(path)).use { it ->
                KomockRunner().run(Yaml().loadAs<KomockConfiguration>(it, KomockConfiguration::class.java))
            }
        } catch (e: IOException) {
            log.error("Unable to read configuration file: ", e)
        }

    }

    fun version(): String {
        return KomockApplication::class.java.classLoader.getResourceAsStream("version.properties").bufferedReader().use { it.readText() }
    }
}
