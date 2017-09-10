package ua.com.lavi.komock.http.handler.after

import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response

/**
 * Created by Oleksandr Loushkin
 */

class EmptyAfterResponseHandlerImpl : AfterResponseHandler {
    override fun handle(request: Request, response: Response) {
        // nothing to do
    }
}
