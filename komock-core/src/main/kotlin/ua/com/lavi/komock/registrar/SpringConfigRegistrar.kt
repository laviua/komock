package ua.com.lavi.komock.registrar

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import ua.com.lavi.komock.engine.Router
import ua.com.lavi.komock.engine.model.ByteResource
import ua.com.lavi.komock.engine.model.PropertySource
import ua.com.lavi.komock.engine.model.SpringConfigResponse
import ua.com.lavi.komock.engine.model.SslKeyStore
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import ua.com.lavi.komock.engine.model.config.spring.SpringConfigProperties
import java.io.IOException
import java.net.BindException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors

/**
 * Created by Oleksandr Loushkin
 * Register Spring configuration files as a separate server instance
 */

class SpringConfigRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val gson = Gson()

    fun register(springConfigProperties: SpringConfigProperties) {
        val httpServerProp: HttpServerProperties = springConfigProperties.httpServer

        var sslKeyStore: SslKeyStore? = null
        if (httpServerProp.ssl.enabled) {
            sslKeyStore = SslKeyStore(
                    ByteResource(Files.readAllBytes(Paths.get(httpServerProp.ssl.keyStoreLocation))),
                    httpServerProp.ssl.keyStorePassword)
        }
        val router = Router(httpServerProp.name,
                httpServerProp.host, httpServerProp.port,
                httpServerProp.minThreads, httpServerProp.maxThreads,
                httpServerProp.idleTimeout, sslKeyStore, httpServerProp.virtualHosts.toMutableList())

        try {
            router.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${httpServerProp.host}, port: ${httpServerProp.port}", e)
            return
        }

        log.info("Started httpServer: ${httpServerProp.name} on port: ${httpServerProp.port}. virtualHosts: ${httpServerProp.virtualHosts}")

        val springConfigFilePathes = Files.walk(Paths.get(springConfigProperties.sourceFolder))
                .filter { it.toFile().isFile }
                .collect(Collectors.toList<Path>())

        for (springConfigFilePath in springConfigFilePathes) {
            val serviceName = extractFilenameFromPath(springConfigFilePath)
            val content = buildContent(springConfigFilePath)
            val propertySources = buildPropertySources(springConfigFilePath, content)
            val springConfigResponse = SpringConfigResponse(serviceName, springConfigProperties.profiles, null, null, propertySources)

            registerSpringConfigurationService(router, springConfigResponse, springConfigFilePath)

        }
    }

    private fun registerSpringConfigurationService(springConfigRouter: Router,
                                                   springConfigResponse: SpringConfigResponse,
                                                   springConfigFile: Path) {

        for (profile in springConfigResponse.profiles) {
            val routeServerProperties = RouteProperties()
            routeServerProperties.code = 200
            routeServerProperties.httpMethod = "GET"
            routeServerProperties.responseBody = gson.toJson(springConfigResponse)
            routeServerProperties.contentType = "application/json"
            routeServerProperties.url = "/${springConfigResponse.name}/$profile"
            springConfigRouter.addRoute(routeServerProperties)
        }
        log.info("Registered spring config file: $springConfigFile")
    }

    private fun buildPropertySources(springConfigFile: Path, propertySourceBody: Map<String, Any>): ArrayList<PropertySource> {
        val propertySources = ArrayList<PropertySource>()
        val propertySourceName = "file:" + springConfigFile.toString().replace("\\\\".toRegex(), "/")
        propertySources.add(PropertySource(propertySourceName, propertySourceBody))
        return propertySources
    }

    @Throws(IOException::class)
    private fun buildContent(springConfigFile: Path): Map<String, Any> {
        val content = String(Files.readAllBytes(springConfigFile), Charset.defaultCharset())
        val map = Yaml().load(content) as Map<String, Any>
        return convert(map, "")
    }

    private fun convert(input: Map<String, Any>, key: String): Map<String, Any> {
        val res = TreeMap<String, Any>()

        for ((key1, value) in input) {
            val newKey = if (key == "") key1 else key + "." + key1

            if (value is Map<*, *>) {
                res.putAll(convert(value as Map<String, Any>, newKey))
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
