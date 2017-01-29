package ua.com.lavi.komock.engine.model

import java.util.HashMap

/**
 * Created by Oleksandr Loushkin
 */

enum class HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, TRACE, CONNECT, OPTIONS;

    // like a cache
    companion object {
        private val methods = HashMap<String, HttpMethod>()

        init {
            values().forEach { methods.put(it.toString(), it) }
        }

        fun retrieveMethod(method: String): HttpMethod = methods[method] ?: throw Exception("Unknown http method")
    }
}
