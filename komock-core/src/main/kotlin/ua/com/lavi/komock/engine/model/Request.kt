package ua.com.lavi.komock.engine.model

import java.util.*
import javax.servlet.http.HttpServletRequest


/**
 * Created by Oleksandr Loushkin
 */

class Request(private val httpServletRequest: HttpServletRequest) {

    fun requestBody(): String {
        return httpServletRequest.inputStream.bufferedReader().use { it.readText() }
    }

    fun queryParametersMap(): Map<String, String> {
        val queryParameters = HashMap<String, String>()
        val queryString = httpServletRequest.queryString

        if (queryString.isNullOrEmpty()) {
            return queryParameters
        }

        val parameters = queryString.split("&".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

        parameters
                .map { it.split("=".toRegex()).dropLastWhile(String::isEmpty).toTypedArray() }
                .forEach {
                    if (it.size == 1) {
                        queryParameters.put(it[0], "")
                    }
                    if (it.size == 2) {
                        queryParameters.put(it[0], it[1])
                    }
                }
        return queryParameters
    }
}
