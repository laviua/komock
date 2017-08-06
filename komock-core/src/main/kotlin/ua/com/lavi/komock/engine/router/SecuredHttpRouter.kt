package ua.com.lavi.komock.engine.router

import ua.com.lavi.komock.engine.model.SslKeyStore
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.server.HttpHandler
import ua.com.lavi.komock.engine.server.JettyServer

/**
 * Created by Oleksandr Loushkin
 * This class represents all logic according to manage server and link route with the server
 */

class SecuredHttpRouter(serverProps: HttpServerProperties, sslKeyStore: SslKeyStore)
    : AbstractHttpRouter(JettyServer(serverProps, HttpHandler(), sslKeyStore))