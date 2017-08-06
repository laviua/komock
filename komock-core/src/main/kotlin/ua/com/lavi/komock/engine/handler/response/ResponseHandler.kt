package ua.com.lavi.komock.engine.handler.response

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

/**
 * Created by Oleksandr Loushkin
 */

interface ResponseHandler {
    fun handle(request: Request, response: Response)
}
