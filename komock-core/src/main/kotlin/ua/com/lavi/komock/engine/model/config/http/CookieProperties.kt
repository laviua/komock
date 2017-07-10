package ua.com.lavi.komock.engine.model.config.http

/**
 * Created by Oleksandr Loushkin
 */

open class CookieProperties {
    var path: String = ""
    var name: String = ""
    var value: String = ""
    var maxAge: Int = 86400 // seconds in day
    var secure: Boolean = false
    var httpOnly: Boolean = false
    var domain: String = ""
    var comment: String = ""
}