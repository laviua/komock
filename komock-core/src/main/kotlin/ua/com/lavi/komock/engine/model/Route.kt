package ua.com.lavi.komock.engine.model

import ua.com.lavi.komock.engine.handler.after.AfterResponseHandler
import ua.com.lavi.komock.engine.handler.before.BeforeResponseHandler
import ua.com.lavi.komock.engine.handler.callback.CallbackHandler
import ua.com.lavi.komock.engine.handler.response.ResponseHandler

/**
 * Created by Oleksandr Loushkin
 */

class Route(val url: String,
            val responseHandler: ResponseHandler,
            val beforeResponseHandler: BeforeResponseHandler,
            val afterResponseHandler: AfterResponseHandler,
            val callbackHandler: CallbackHandler)