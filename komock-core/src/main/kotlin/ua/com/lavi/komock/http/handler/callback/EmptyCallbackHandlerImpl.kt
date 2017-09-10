package ua.com.lavi.komock.http.handler.callback

import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response

/**
 * Created by Oleksandr Loushkin
 */

class EmptyCallbackHandlerImpl : CallbackHandler {

    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
