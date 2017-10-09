package ua.com.lavi.komock.model.config.http

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import java.net.URI

/**
 * Created by Oleksandr Loushkin on 30.03.17.
 */

open class CallbackRequest(private val methodName: String,
                           private val uri: String) : HttpEntityEnclosingRequestBase() {

    override fun getMethod(): String {
        return methodName
    }

    override fun getURI(): URI {
        return URI.create(uri)
    }
}
