package ua.com.lavi.komock.http.handler.before

import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response

/**
 * Created by Oleksandr Loushkin
 */

class EmptyBeforeResponseHandlerImpl : BeforeResponseHandler {

    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
