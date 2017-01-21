package ua.com.lavi.komock.engine.model

import javax.servlet.http.HttpServletRequest

/**
 * Created by Oleksandr Loushkin
 */

class Request(private val httpServletRequest: HttpServletRequest) {

    fun requestBody(): String {
        return httpServletRequest.inputStream.bufferedReader().use { it.readText() }
    }
}
