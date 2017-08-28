package ua.com.lavi.komock.engine.model

import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * Created by Oleksandr Loushkin
 */

class Request(private val httpServletRequest: HttpServletRequest) {

    fun getRequestBody(): String {
        return httpServletRequest.inputStream.bufferedReader().use { it.readText() }
    }

    fun getHttpServletRequest(): HttpServletRequest {
        return httpServletRequest
    }

    fun getQueryParametersMap(): Map<String, String> {
        val queryParameters = HashMap<String, String>()
        val queryString = httpServletRequest.queryString

        if (queryString.isNullOrEmpty()) {
            return queryParameters
        }

        queryString
                .split("&".toRegex())
                .dropLastWhile(String::isEmpty)
                .toTypedArray()
                .map { it.split("=".toRegex())
                .dropLastWhile(String::isEmpty)
                .toTypedArray() }
                .filter { it.size == 2 }
                .forEach { queryParameters.put(it[0], it[1]) }

        return queryParameters
    }

    fun getHeaders(): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        for (headerName in httpServletRequest.headerNames) {
            headers.put(headerName, httpServletRequest.getHeader(headerName))
        }
        return headers
    }
}
