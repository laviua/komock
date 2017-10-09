package ua.com.lavi.komock.http.handler.response

import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response

/**
 * Created by Oleksandr Loushkin
 */

class EmptyResponseHandler : ResponseHandler {

    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
