package ua.com.lavi.komock.registrar.proxy

import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.entity.InputStreamEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.http.handler.response.ResponseHandler
import ua.com.lavi.komock.http.server.SecuredMockServer
import ua.com.lavi.komock.http.server.UnsecuredMockServer
import ua.com.lavi.komock.model.HttpMethod
import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response
import ua.com.lavi.komock.model.config.http.CallbackRequest
import ua.com.lavi.komock.model.config.http.HttpServerProperties
import ua.com.lavi.komock.model.config.http.RouteProperties
import ua.com.lavi.komock.model.config.proxy.ProxyConfigProperties
import ua.com.lavi.komock.registrar.Registrar
import java.io.File
import java.net.BindException


/**
 * Created by Oleksandr Loushkin
 * Register proxy interceptor configuration files as a separate http server with default forward rule
 */

class ProxyForwarder : Registrar<ProxyConfigProperties> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val yamlRouteWriter = YamlRouteWriter()

    private val ignoredHeaders = arrayListOf("Content-Length", "Host", "Connection", "Date")

    override fun register(properties: ProxyConfigProperties) {
        val httpServerProp: HttpServerProperties = properties.httpServer

        val router = if (httpServerProp.ssl.enabled) {
            SecuredMockServer(httpServerProp)
        } else {
            UnsecuredMockServer(httpServerProp)
        }

        try {
            router.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${httpServerProp.host}, port: ${httpServerProp.port}", e)
            return
        }

        val httpClient = HttpClients.custom()
                .setSSLContext(SSLContextBuilder().loadTrustMaterial(null) { _, _ -> true }.build())
                .setSSLHostnameVerifier(NoopHostnameVerifier())
                .build()

        val hitTable = hashMapOf<String, MutableMap<String, RouteProperties>>()

        val responseHandler = object : ResponseHandler {
            override fun handle(request: Request, response: Response) {
                val httpResponse: CloseableHttpResponse = httpClient.execute(buildHttpRequest(request, properties))

                //fill the response model
                val content = httpResponse.entity.content.bufferedReader().use { it.readText() }
                response.setCode(httpResponse.statusLine.statusCode)
                response.setContent(content)
                for (header in httpResponse.allHeaders) {
                    response.addHeader(header.name, header.value)
                }
                log.debug("Received: $httpResponse \r\n $content")

                // dump the routes in the komock format
                if (properties.writeTo.isNotEmpty()) {

                    val routeProperties = buildRouteProperties(request, response)

                    var methodRoutes: MutableMap<String, RouteProperties>? = hitTable[request.getMethod()]
                    if (methodRoutes == null) {
                        methodRoutes = hashMapOf()
                        hitTable[request.getMethod()] = methodRoutes
                    }
                    val requestURI = request.getHttpServletRequest().requestURI
                    methodRoutes[requestURI] = routeProperties

                    yamlRouteWriter.write(hitTable, File(properties.writeTo))
                }

            }
        }

        for (httpMethod in HttpMethod.values()) {
            router.addRoute("/**", httpMethod, responseHandler)
        }
    }

    private fun buildRouteProperties(request: Request, response: Response): RouteProperties {
        val routeProperties = RouteProperties()
        routeProperties.url = request.getHttpServletRequest().requestURI
        routeProperties.httpMethod = request.getMethod()
        routeProperties.responseBody = "'${response.getContent().replace("\n", "")}'"
        routeProperties.code = response.getCode()
        routeProperties.responseHeaders = response.getHeaders().filterNot { ignoreHeader(it.key) }
        return routeProperties
    }

    private fun buildHttpRequest(request: Request, properties: ProxyConfigProperties): HttpUriRequest {
        val baseUri = "${properties.forwardTo}${request.getHttpServletRequest().requestURI}"
        val queryUri = buildQueryUrl(request)
        val requestUri = "$baseUri$queryUri"
        log.debug("Request uri: ${request.getMethod()}: $requestUri")
        val anyRequest = CallbackRequest(request.getMethod(), requestUri)

        request.getHeaders()
                .filterNot { ignoreHeader(it.key) }
                .forEach { header -> anyRequest.addHeader(header.key, header.value) }

        // add body to the request. it needs for the POST callback
        if (request.getRequestBody().isNotBlank()) {
            anyRequest.entity = InputStreamEntity(request.getHttpServletRequest().inputStream)
        }
        anyRequest.config = RequestConfig.custom()
                .setConnectTimeout(properties.connectTimeout)
                .setConnectionRequestTimeout(properties.connectionRequestTimeout)
                .setSocketTimeout(properties.socketTimeout)
                .build()
        return anyRequest
    }

    private fun buildQueryUrl(request: Request): String {
        return request.getQueryParametersMap().entries.stream()
                .map { p -> p.key + "=" + p.value }
                .reduce { p1, p2 -> "$p1&$p2" }
                .map { s -> "?$s" }
                .orElse("")
    }

    private fun ignoreHeader(headerName: String): Boolean {
        if (ignoredHeaders.contains(headerName)) {
            return true
        }
        return false
    }
}
