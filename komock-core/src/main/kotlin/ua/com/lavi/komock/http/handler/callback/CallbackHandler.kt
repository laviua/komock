package ua.com.lavi.komock.http.handler.callback

import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response

/**
 * Created by Oleksandr Loushkin on 30.03.17.
 */

interface CallbackHandler {
    fun handle(request: Request, response: Response)
}