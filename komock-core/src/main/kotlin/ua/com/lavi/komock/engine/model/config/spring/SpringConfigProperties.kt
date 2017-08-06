package ua.com.lavi.komock.engine.model.config.spring

import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

open class SpringConfigProperties {
    var enabled: Boolean = false
    var refreshPeriod: Long = 0
    var profiles: List<String> = ArrayList()
    var sourceFolder: String = "/"
    var httpServer: HttpServerProperties = HttpServerProperties()
}
