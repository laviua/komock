package ua.com.lavi.komock.engine.model.config.spring

import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

open class SpringConfigProperties {
    open var enabled: Boolean = false
    open var profiles:List<String> = ArrayList()
    open var sourceFolder: String = "/"
    open var httpServer: HttpServerProperties = HttpServerProperties()
}
