package ua.com.lavi.komock.engine.model

import ua.com.lavi.komock.engine.model.config.http.CookieProperties
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

/**
 * Created by Oleksandr Loushkin
 */

class Response(private val servletResponse: HttpServletResponse) {

    var content: String = ""

    fun code(code: Int) {
        servletResponse.status = code
    }

    fun contentType(contentType: String?) {
        servletResponse.contentType = contentType
    }

    fun addHeader(headerName: String, headerValue: String) {
        servletResponse.addHeader(headerName, headerValue)
    }

    fun addCookie(cookieProperties: CookieProperties) {
        val cookie = Cookie(cookieProperties.name, cookieProperties.value)
        cookie.path = cookieProperties.path
        cookie.maxAge = cookieProperties.maxAge
        cookie.secure = cookieProperties.secure
        cookie.isHttpOnly = cookieProperties.httpOnly
        cookie.domain = cookieProperties.domain
        cookie.comment = cookieProperties.comment
        servletResponse.addCookie(cookie)
    }

    fun getHeaders(): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        for (headerName in servletResponse.headerNames) {
            headers.put(headerName, servletResponse.getHeader(headerName))
        }
        return headers
    }
}
