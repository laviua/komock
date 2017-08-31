package ua.com.lavi.komock.engine.handler.response

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response

/**
 * Created by Oleksandr Loushkin
 */

class EmptyResponseHandler : ResponseHandler {

    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
