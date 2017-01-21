package ua.com.lavi.komock.config.property.spring

import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

data class SpringConfigResponse(val name: String,
                                val profiles: ArrayList<String>,
                                val label: String?,
                                val version: String?,
                                val propertySources: ArrayList<PropertySource>)

data class PropertySource(val name: String,
                          val source: Any)