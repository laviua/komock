package ua.com.lavi.komock.engine.handler


import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

/**
 * Created by Oleksandr Loushkin
 */

interface BeforeRequestHandler {
    fun handle(request: Request, response: Response)
}
