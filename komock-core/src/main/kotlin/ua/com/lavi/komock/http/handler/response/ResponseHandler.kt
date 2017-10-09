package ua.com.lavi.komock.http.handler.response

import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response

/**
 * Created by Oleksandr Loushkin
 */

interface ResponseHandler {
    fun handle(request: Request, response: Response)
}
