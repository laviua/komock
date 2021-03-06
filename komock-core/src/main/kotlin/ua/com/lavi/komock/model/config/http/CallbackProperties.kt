package ua.com.lavi.komock.model.config.http

/**
 * Created by Oleksandr Loushkin on 30.03.17.
 */

open class CallbackProperties {
    var enabled: Boolean = false
    var httpMethod: String = ""
    var url: String = ""
    var requestBody: String = ""
    var connectTimeout: Int = 250000
    var connectionRequestTimeout: Int = 30000
    var socketTimeout: Int = 25000
    var delay: Long = 0
    var requestHeaders: Map<String, String> = HashMap()
}