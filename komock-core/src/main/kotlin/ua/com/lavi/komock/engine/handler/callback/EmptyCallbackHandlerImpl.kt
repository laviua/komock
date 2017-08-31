package ua.com.lavi.komock.engine.handler.callback

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

/**
 * Created by Oleksandr Loushkin
 */

class EmptyCallbackHandlerImpl : CallbackHandler {

    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
