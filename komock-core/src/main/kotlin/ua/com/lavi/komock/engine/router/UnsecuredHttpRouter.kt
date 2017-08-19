package ua.com.lavi.komock.engine.router

import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.server.UnsecuredJettyServer

/**
 * Created by Oleksandr Loushkin on 05.08.17.
 */

class UnsecuredHttpRouter(serverProps: HttpServerProperties) : AbstractHttpRouter(UnsecuredJettyServer(serverProps))