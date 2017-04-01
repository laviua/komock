package ua.com.lavi.komock.registrar.spring

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import ua.com.lavi.komock.engine.Router
import ua.com.lavi.komock.engine.model.*
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import ua.com.lavi.komock.engine.model.config.spring.SpringConfigProperties
import java.net.BindException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

/**
 * Created by Oleksandr Loushkin
 * Register Spring configuration files as a separate server instance
 */

class SpringConfigRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val gson = Gson()
    private val configWatcher = SpringConfigWatcher()

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

        val profiles = springConfigProperties.profiles

        springConfigFilePathes.forEach({registerSpringConfigurationPath(it, profiles, router)})

        if (springConfigProperties.refreshPeriod > 0) {

            val listener = object : FileListener {
                override fun onChange(filePath: Path) {
                    unRegisterSpringConfigurationPath(filePath, profiles, router)
                    registerSpringConfigurationPath(filePath, profiles, router)
                }
            }

            configWatcher.watchFiles(springConfigFilePathes, springConfigProperties.refreshPeriod)
            configWatcher.setListeners(arrayListOf(listener))
            configWatcher.start()
        }

    }

    private fun registerSpringConfigurationPath(springConfigFilePath: Path,
                                                profiles:List<String>,
                                                router: Router) {
        val serviceName = extractFilenameFromPath(springConfigFilePath)
        val textData = String(Files.readAllBytes(springConfigFilePath), Charsets.UTF_8)
        val content:Map<String,Any> = buildFlatMap(textData)
        val propertySources = buildPropertySources(springConfigFilePath, content)
        val springConfigResponse = SpringConfigResponse(serviceName, profiles, null, null, propertySources)

        val jsonConfigResponse = gson.toJson(springConfigResponse)

        for (profile in profiles) {

            val routeServerProperties = RouteProperties()
            routeServerProperties.code = 200
            routeServerProperties.httpMethod = "GET"
            routeServerProperties.responseBody = jsonConfigResponse
            routeServerProperties.contentType = "application/json"
            routeServerProperties.url = "/$serviceName/$profile"

            router.addRoute(routeServerProperties)
        }

        log.info("Registered spring config file: $springConfigFilePath")
    }

    private fun unRegisterSpringConfigurationPath(springConfigFilePath: Path, profiles: List<String>, router: Router) {
        profiles.forEach({
            val serviceName = extractFilenameFromPath(springConfigFilePath)
            val url = "/$serviceName/$it"
            router.deleteRoute(url, HttpMethod.GET)
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

    private fun convertPropertyMap(input: Map<String, Any>, key: String): Map<String, Any> {
        val res = TreeMap<String, Any>()

        for ((key1, value) in input) {
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
