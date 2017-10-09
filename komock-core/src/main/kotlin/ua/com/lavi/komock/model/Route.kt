package ua.com.lavi.komock.model

import ua.com.lavi.komock.http.handler.after.AfterResponseHandler
import ua.com.lavi.komock.http.handler.before.BeforeResponseHandler
import ua.com.lavi.komock.http.handler.callback.CallbackHandler
import ua.com.lavi.komock.http.handler.response.ResponseHandler

/**
 * Created by Oleksandr Loushkin
 */

class Route(val url: String,
            val responseHandler: ResponseHandler,
            val beforeResponseHandler: BeforeResponseHandler,
            val afterResponseHandler: AfterResponseHandler,
            val callbackHandler: CallbackHandler)