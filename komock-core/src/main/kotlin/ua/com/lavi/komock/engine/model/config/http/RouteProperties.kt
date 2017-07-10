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
    var responseHeaders:Map<String,String> = HashMap()
    var cookies: List<CookieProperties> = ArrayList()
    var code: Int = 0
    var logRequest: Boolean = false
    var logRequestTemplate: String = "url: \${url}. body: \${body}"
    var logResponseTemplate: String = "url: \${url}. body: \${body}"
    var logResponse: Boolean = false
    var logBefore: String = ""
    var logAfter: String = ""
    var headerAuth: HeaderAuthProperties = HeaderAuthProperties()
    var callback: CallbackProperties = CallbackProperties()
    var delay: Long = 0
}