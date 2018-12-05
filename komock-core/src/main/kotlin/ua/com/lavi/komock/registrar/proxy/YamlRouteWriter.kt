package ua.com.lavi.komock.registrar.proxy

import com.google.gson.Gson
import ua.com.lavi.komock.model.config.http.RouteProperties
import java.io.File

class YamlRouteWriter {

    private val gson = Gson()

    fun write(routeMap: Map<String, MutableMap<String, RouteProperties>>, targetFile: File) {

        //check that possible to write
        targetFile.writeText("")

        val routes: List<RouteProperties> = routeMap.flatMap { it.value.values }

        for (route in routes) {

            val url = "url: ${route.url}"
            val headers = "responseHeaders: ${gson.toJson(route.responseHeaders)}"
            val httpMethod = "httpMethod: ${route.httpMethod}"
            val httpCode = "code: ${route.code}"
            val responseBody = "responseBody: ${route.responseBody.replace("\n", "")}"

            val template = "-\r\n" +
                    "  $httpMethod \r\n" +
                    "  $url \r\n" +
                    "  $headers \r\n" +
                    "  $httpCode \r\n" +
                    "  $responseBody \r\n"

            targetFile.appendText(template)
        }
    }
}