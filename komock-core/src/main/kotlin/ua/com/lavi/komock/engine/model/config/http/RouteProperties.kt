package ua.com.lavi.komock.engine.model.config.http


import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

open class RouteProperties {
    var enabled: Boolean = true
    var httpMethod: String = ""
    var url: String = ""
    var contentType: String = ""
    var responseBody: String = ""
    var responseHeaders:List<Map<String,String>> = ArrayList()
    var cookies: List<CookieProperties> = ArrayList()
    var code: Int = 0
    var logRequest: Boolean = false
    var logResponse: Boolean = false
    var logBefore: String = ""
    var logAfter: String = ""
    var headerAuth: HeaderAuthProperties = HeaderAuthProperties()
}