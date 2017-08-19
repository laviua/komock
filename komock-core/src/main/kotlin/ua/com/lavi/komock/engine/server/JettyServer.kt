package ua.com.lavi.komock.engine.server

import ua.com.lavi.komock.engine.router.RoutingTable

/**
 * Created by Oleksandr Loushkin on 19.08.17.
 */
interface JettyServer {

    fun start()
    fun stop()
    fun addVirtualHosts(virtualHosts: List<String>)
    fun removeVirtualHosts(virtualHosts: List<String>)
    fun routingTable(): RoutingTable
}