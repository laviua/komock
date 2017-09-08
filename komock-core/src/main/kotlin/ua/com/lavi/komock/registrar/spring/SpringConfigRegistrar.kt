package ua.com.lavi.komock.registrar.spring

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.PropertySource
import ua.com.lavi.komock.engine.model.SpringConfigResponse
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import ua.com.lavi.komock.engine.model.config.spring.SpringConfigProperties
import ua.com.lavi.komock.engine.server.MockServer
import ua.com.lavi.komock.engine.server.SecuredMockServer
import ua.com.lavi.komock.engine.server.UnsecuredMockServer
import ua.com.lavi.komock.registrar.FileChangeHandler
import ua.com.lavi.komock.registrar.FileChangeWatcher
import ua.com.lavi.komock.registrar.Registrar
import java.net.BindException
import java.nio.file.Path
import java.util.*

/**
 * Created by Oleksandr Loushkin
 * Register Spring configuration files as a separate http server instance
 */

class SpringConfigRegistrar: Registrar<SpringConfigProperties> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val gson = Gson()

    override fun register(springConfigProperties: SpringConfigProperties) {
        val httpServerProp: HttpServerProperties = springConfigProperties.httpServer

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

        val profiles = springConfigProperties.profiles

        val fileList = springConfigProperties.fileList()
        fileList.forEach({ configFilePath -> registerPath(configFilePath, profiles, router) })

        if (springConfigProperties.doRefresh()) {
            val fileChangeHandler = object : FileChangeHandler {
                override fun onFileChange(filePath: Path) {
                    unregisterPath(filePath, profiles, router)
                    registerPath(filePath, profiles, router)
                }
            }
            FileChangeWatcher(fileChangeHandler, fileList, springConfigProperties.refreshPeriod).start()
        }
    }

    private fun unregisterPath(configFilePath: Path, profiles: List<String>, server: MockServer) {
        profiles.forEach({
            val serviceName = extractFilenameFromPath(configFilePath)
            val url = "/$serviceName/$it"
            server.deleteRoute(url, HttpMethod.GET)
        })
        log.info("Unregistered spring config file: $configFilePath")
    }

    private fun registerPath(configFilePath: Path,
                             profiles: List<String>,
                             server: MockServer) {
        val serviceName = extractFilenameFromPath(configFilePath)
        val springConfigResponse = SpringConfigResponse(serviceName, profiles, buildPropertySources(configFilePath))

        profiles.forEach { profile -> server.addRoute(buildRouteProperties(springConfigResponse, serviceName, profile)) }

        log.info("Registered spring config file: $configFilePath")
    }

    private fun buildRouteProperties(springConfigResponse: SpringConfigResponse, serviceName: String, profile: String): RouteProperties {
        val routeServerProperties = RouteProperties()
        routeServerProperties.code = 200
        routeServerProperties.httpMethod = HttpMethod.GET.name
        routeServerProperties.responseBody = gson.toJson(springConfigResponse)
        routeServerProperties.contentType = "application/json"
        routeServerProperties.url = "/$serviceName/$profile"
        return routeServerProperties
    }

    private fun buildPropertySources(springConfigFile: Path): ArrayList<PropertySource> {
        val propertySourceName = "file:" + springConfigFile.toString().replace("\\\\".toRegex(), "/")
        val propertySourceBody = buildFlatMap(springConfigFile.toFile().readText())
        return arrayListOf(PropertySource(propertySourceName, propertySourceBody))
    }

    private fun buildFlatMap(textData: String): Map<String, Any> {
        val map = Yaml().load(textData) as Map<String, Any>
        return convertPropertyMap(map, "")
    }

    private fun convertPropertyMap(inputMap: Map<String, Any>, key: String): Map<String, Any> {
        val res = TreeMap<String, Any>()

        for ((key1, value) in inputMap) {
            val newKey = if (key == "") key1 else key + "." + key1

            if (value is Map<*, *>) {
                res.putAll(convertPropertyMap(value as Map<String, Any>, newKey))
            } else {
                res.put(newKey, value)
            }
        }
        return res
    }

    private fun extractFilenameFromPath(filePath: Path): String {
        val path = filePath.fileName ?: throw RuntimeException("File is not exists")
        return path.toString().replace(".yml".toRegex(), "")
    }
}
