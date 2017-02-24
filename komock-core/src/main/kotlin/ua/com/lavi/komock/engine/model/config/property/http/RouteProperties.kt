package ua.com.lavi.komock.engine.model.config.property.http


import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class RouteProperties {
    var enabled: Boolean = true
    var httpMethod: String = ""
    var url: String = ""
    var contentType: String = ""
    var responseBody: String = ""
    var responseHeaders = ArrayList<Map<String, String>>()
    var cookies: List<CookieProperties> = ArrayList()
    var code: Int = 0
    var logRequest: Boolean = false
    var logResponse: Boolean = false
    var logBefore: String = ""
    var logAfter: String = ""
}

class CookieProperties {
    var path: String = ""
    var name: String = ""
    var value: String = ""
    var maxAge: Int = 86400 // seconds in day
    var secure: Boolean = false
    var httpOnly: Boolean = false
    var domain: String = ""
    var comment: String = ""
}