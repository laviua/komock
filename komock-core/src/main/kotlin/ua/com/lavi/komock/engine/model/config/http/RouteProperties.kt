package ua.com.lavi.komock.engine.model.config.http


import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

open class RouteProperties {
    open var enabled: Boolean = true
    open var httpMethod: String = ""
    open var url: String = ""
    open var contentType: String = ""
    open var responseBody: String = ""
    open var responseHeaders:List<Map<String,String>> = ArrayList()
    open var cookies: List<CookieProperties> = ArrayList()
    open var code: Int = 0
    open var logRequest: Boolean = false
    open var logResponse: Boolean = false
    open var logBefore: String = ""
    open var logAfter: String = ""
}

open class CookieProperties {
    open var path: String = ""
    open var name: String = ""
    open var value: String = ""
    open var maxAge: Int = 86400 // seconds in day
    open var secure: Boolean = false
    open var httpOnly: Boolean = false
    open var domain: String = ""
    open var comment: String = ""
}