package ua.com.lavi.komock.engine.model

import ua.com.lavi.komock.engine.handler.AfterResponseHandler
import ua.com.lavi.komock.engine.handler.BeforeResponseHandler
import ua.com.lavi.komock.engine.handler.CallbackHandler
import ua.com.lavi.komock.engine.handler.ResponseHandler

/**
 * Created by Oleksandr Loushkin
 */

class Route(val url: String,
            val httpMethod: HttpMethod,
            val responseHandler: ResponseHandler,
            val beforeResponseHandlers: List<BeforeResponseHandler>,
            val afterResponseHandlers: List<AfterResponseHandler>,
            val callbackHandler: CallbackHandler)