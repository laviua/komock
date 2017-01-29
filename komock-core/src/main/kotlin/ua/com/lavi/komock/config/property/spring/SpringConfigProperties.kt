package ua.com.lavi.komock.config.property.spring

import ua.com.lavi.komock.config.property.http.ServerProperties
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class SpringConfigProperties {
    var enabled: Boolean = false
    var profiles = ArrayList<String>()
    var sourceFolder: String = "/"
    var server: ServerProperties = ServerProperties()
}