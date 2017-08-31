package ua.com.lavi.komock.engine.model.config.spring

import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors

/**
 * Created by Oleksandr Loushkin
 */

open class SpringConfigProperties {
    var enabled: Boolean = false
    var refreshPeriod: Long = 0
    var profiles: List<String> = ArrayList()
    var sourceFolder: String = "/"
    var httpServer: HttpServerProperties = HttpServerProperties()

    fun doRefresh(): Boolean {
        return refreshPeriod > 0
    }

    fun fileList() : List<Path> {
        return Files.walk(Paths.get(sourceFolder))
                .filter { it.toFile().isFile }
                .collect(Collectors.toList<Path>())
    }
}
