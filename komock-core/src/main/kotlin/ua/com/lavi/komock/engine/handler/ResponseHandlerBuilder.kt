package ua.com.lavi.komock.engine.handler

import ua.com.lavi.komock.engine.model.config.http.RouteProperties


/**
 * Class represents logic for request performing
 * Created by Oleksandr Loushkin
 */

class ResponseHandlerBuilder(val routeProperties: RouteProperties) {

    fun beforeRouteHandlers(): List<BeforeResponseHandler> {
        return arrayListOf(LogBeforeResponseHandler(routeProperties))
    }

    fun afterRequestHandlers(): List<AfterResponseHandler> {
        return arrayListOf(LogAfterResponseHandler(routeProperties))
    }

    fun routeHandler(): ResponseHandler {
        return ResponseBodyHandler(routeProperties)
    }

    /**
     * Invoke http call by apache http client
     */
    fun callbackHandler(): CallbackHandler {
        return RestCallbackHandler(routeProperties)
    }
}
