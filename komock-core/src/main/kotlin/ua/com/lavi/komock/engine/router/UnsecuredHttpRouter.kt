package ua.com.lavi.komock.engine.router

import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.server.HttpHandler
import ua.com.lavi.komock.engine.server.JettyServer

/**
 * Created by Oleksandr Loushkin on 05.08.17.
 */

class UnsecuredHttpRouter(serverProps: HttpServerProperties) : AbstractHttpRouter(JettyServer(serverProps, HttpHandler()))