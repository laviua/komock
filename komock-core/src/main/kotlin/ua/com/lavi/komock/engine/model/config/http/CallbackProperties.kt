package ua.com.lavi.komock.engine.model.config.http

/**
 * Created by Oleksandr Loushkin on 30.03.17.
 */
class CallbackProperties {
    var enabled: Boolean = false
    var httpMethod: String = ""
    var url: String = ""
    var requestBody: String = ""
    var connectTimeout: Int = 250000
    var connectionRequestTimeout: Int = 30000
    var socketTimeout: Int = 25000
    var requestHeaders: Map<String, String> = HashMap()
}