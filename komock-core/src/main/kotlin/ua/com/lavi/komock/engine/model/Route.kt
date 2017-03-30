package ua.com.lavi.komock.engine.model

import ua.com.lavi.komock.engine.handler.AfterRequestHandler
import ua.com.lavi.komock.engine.handler.BeforeRequestHandler
import ua.com.lavi.komock.engine.handler.CallbackHandler
import ua.com.lavi.komock.engine.handler.RequestHandler

/**
 * Created by Oleksandr Loushkin
 */

class Route(val url: String,
            val httpMethod: HttpMethod,
            val requestHandler: RequestHandler,
            val beforeRequestHandler: BeforeRequestHandler,
            val afterRequestHandler: AfterRequestHandler,
            val callbackHandler: CallbackHandler)