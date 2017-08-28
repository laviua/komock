package ua.com.lavi.komock.engine.handler.before

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

class EmptyBeforeResponseHandlerImpl : BeforeResponseHandler {

    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
