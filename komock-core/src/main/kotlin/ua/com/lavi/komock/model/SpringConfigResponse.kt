package ua.com.lavi.komock.model

import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

data class SpringConfigResponse(val name: String,
                                val profiles: List<String> = ArrayList(),
                                val label: String?,
                                val version: String?,
                                val propertySources: List<PropertySource> = ArrayList()) {

    constructor(name: String, profiles: List<String>, propertySources: List<PropertySource>) : this(name, profiles, null, null, propertySources)
}

data class PropertySource(val name: String,
                          val source: Any)