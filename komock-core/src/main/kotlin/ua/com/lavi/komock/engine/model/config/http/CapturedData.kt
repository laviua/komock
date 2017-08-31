package ua.com.lavi.komock.engine.model.config.http

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

/**
 * Created by Oleksandr Loushkin on 20.08.17.
 */

data class CapturedData(private val request: Request, private val response: Response) {

    val requestBody: String = request.getRequestBody()
    val requestHeaders: Map<String, String> = request.getHeaders()
    val responseBody: String = response.getContent()
    val responseHeaders: Map<String, String> = response.getHeaders()
}