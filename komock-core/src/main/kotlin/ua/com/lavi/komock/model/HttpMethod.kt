package ua.com.lavi.komock.model

/**
 * Created by Oleksandr Loushkin
 */

enum class HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, TRACE, CONNECT, OPTIONS;

    companion object {
        fun retrieveMethod(method: String): HttpMethod = valueOf(method)
    }
}