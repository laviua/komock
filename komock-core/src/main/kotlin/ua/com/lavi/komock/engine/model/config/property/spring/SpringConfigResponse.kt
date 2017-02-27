package ua.com.lavi.komock.engine.model.config.property.spring

import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

data class SpringConfigResponse(val name: String,
                                val profiles:List<String> = ArrayList(),
                                val label: String?,
                                val version: String?,
                                val propertySources:List<PropertySource> = ArrayList())

data class PropertySource(val name: String,
                          val source: Any)