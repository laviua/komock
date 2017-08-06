package ua.com.lavi.komock.engine.handler.before

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

/**
 * Created by Oleksandr Loushkin
 */

interface BeforeResponseHandler {
    fun handle(request: Request, response: Response)
}
