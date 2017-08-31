package ua.com.lavi.komock.engine.handler.before

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

/**
 * Created by Oleksandr Loushkin
 */

class EmptyBeforeResponseHandlerImpl : BeforeResponseHandler {

    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
