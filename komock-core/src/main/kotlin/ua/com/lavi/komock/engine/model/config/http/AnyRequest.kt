package ua.com.lavi.komock.engine.model.config.http

import org.apache.http.client.methods.HttpRequestBase
import java.net.URI

/**
 * Created by Oleksandr Loushkin on 30.03.17.
 */
class AnyRequest(val methodName:String, val uri: String) : HttpRequestBase() {

    override fun getMethod(): String {
        return methodName
    }

    override fun getURI(): URI {
        return URI.create(uri)
    }
}
