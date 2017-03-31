package ua.com.lavi.komock.engine.model.config.http

/**
 * Created by Oleksandr Loushkin on 30.03.17.
 */
class CallbackProperties {
    var enabled: Boolean = false
    var httpMethod: String = ""
    var url: String = ""
    var requestBody: String = ""
    var requestHeaders:Map<String,String> = HashMap()
}