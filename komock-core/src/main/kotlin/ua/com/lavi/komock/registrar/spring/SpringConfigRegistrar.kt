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
import java.net.BindException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Oleksandr Loushkin
 * Register Spring configuration files as a separate server instance
 */

class SpringConfigRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val gson = Gson()

    fun register(springConfigProperties: SpringConfigProperties) {
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

        if (springConfigProperties.refreshPeriod > 0) {

            val fileListener = object : FileChangeHandler {
                override fun onFileChange(filePath: Path) {
                    unregisterPath(filePath, profiles, router)
                    registerPath(filePath, profiles, router)
                }
            }
            FileChangeWatcher(fileListener, fileList, springConfigProperties.refreshPeriod).start()
        }

    }

    private fun registerPath(configFilePath: Path,
                             profiles: List<String>,
                             server: MockServer) {
        val serviceName = extractFilenameFromPath(configFilePath)
        val textData = String(Files.readAllBytes(configFilePath), Charsets.UTF_8)
        val content: Map<String, Any> = buildFlatMap(textData)
        val propertySources = buildPropertySources(configFilePath, content)
        val springConfigResponse = SpringConfigResponse(serviceName, profiles, null, null, propertySources)

        val jsonConfigResponse = gson.toJson(springConfigResponse)

        for (profile in profiles) {

            val routeServerProperties = RouteProperties()
            routeServerProperties.code = 200
            routeServerProperties.httpMethod = "GET"
            routeServerProperties.responseBody = jsonConfigResponse
            routeServerProperties.contentType = "application/json"
            routeServerProperties.url = "/$serviceName/$profile"

            server.addRoute(routeServerProperties)
        }

        log.info("Registered spring config file: $configFilePath")
    }

    private fun unregisterPath(springConfigFilePath: Path, profiles: List<String>, server: MockServer) {
        profiles.forEach({
            val serviceName = extractFilenameFromPath(springConfigFilePath)
            val url = "/$serviceName/$it"
            server.deleteRoute(url, HttpMethod.GET)
        })
    }

    private fun buildPropertySources(springConfigFile: Path, propertySourceBody: Map<String, Any>): ArrayList<PropertySource> {
        val propertySources = ArrayList<PropertySource>()
        val propertySourceName = "file:" + springConfigFile.toString().replace("\\\\".toRegex(), "/")
        propertySources.add(PropertySource(propertySourceName, propertySourceBody))
        return propertySources
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
