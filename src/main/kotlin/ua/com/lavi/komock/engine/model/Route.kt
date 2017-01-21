package ua.com.lavi.komock.engine.model

import ua.com.lavi.komock.engine.handler.AfterRouteHandler
import ua.com.lavi.komock.engine.handler.BeforeRouteHandler
import ua.com.lavi.komock.engine.handler.RouteHandler

/**
 * Created by Oleksandr Loushkin
 */

class Route(val url: String,
            val httpMethod: HttpMethod,
            val routeHandler: RouteHandler,
            val beforeRouteHandler: BeforeRouteHandler,
            val afterRouteHandler: AfterRouteHandler)
