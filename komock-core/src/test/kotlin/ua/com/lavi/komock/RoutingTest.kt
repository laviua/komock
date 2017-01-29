package ua.com.lavi.komock

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.yaml.snakeyaml.Yaml
import ua.com.lavi.komock.config.ApplicationConfiguration
import ua.com.lavi.komock.engine.Router
import ua.com.lavi.komock.engine.RoutingTable
import ua.com.lavi.komock.engine.handler.AfterRouteHandler
import ua.com.lavi.komock.engine.handler.BeforeRouteHandler
import ua.com.lavi.komock.engine.handler.RouteHandler
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.fail


/**
 * Created by Oleksandr Loushkin
 */

class RoutingTest {

    companion object {

        private const val MOCK_EXAMPLE_YAML = "mock_example.yml"

        @BeforeClass @JvmStatic
        fun startServer() {
            runApplication(MOCK_EXAMPLE_YAML)
        }

        private fun runApplication(path: String) {
            Files.newInputStream(Paths.get(path)).use { it ->
                KomockRunner().run(Yaml().loadAs<ApplicationConfiguration>(it, ApplicationConfiguration::class.java))
            }
        }

        @AfterClass @JvmStatic
        fun stopServer() {
            Router.stopAllRouters()
        }
    }

    @Test
    @Throws(UnirestException::class)
    fun should_ok_get_noContent() {

        val response = Unirest.get("http://127.0.0.1:8081/testNoConent").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 204)
        assertTrue(response.body == null)
    }

    @Test
    @Throws(UnirestException::class)
    fun should_ok_get_plaintext_with_content() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetText").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertTrue(response.body == "Hello World. Plain Text.")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_ok_post_plaintext_with_content() {

        val response = Unirest.post("http://127.0.0.1:8081/testGetText").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertTrue(response.body == "Hello World Again. Plain Text.")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_ok_get_plaintext_with_content_by_url_mask_and_additional_text() {

        val response = Unirest.get("http://127.0.0.1:8081/anymask/blablabla/anypath/").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertTrue(response.body == "Hello World. Test url mask with additional text")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_ok_get_plaintext_with_content_by_url_mask_and_additional_text2() {

        val response = Unirest.get("http://127.0.0.1:8081/somemask/xxx/somepath/yyy/somepath").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertTrue(response.body == "Some Complicated Path")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_not_ok_get_plaintext_with_content_by_url_mask_and_additional_text2() {

        val response = Unirest.get("http://127.0.0.1:8081/somemas/xxx/somepath/yyy/somepath").asString()

        assertTrue(response.status == 404)
    }


    @Test
    @Throws(UnirestException::class)
    fun should_not_found_get_plaintext_with_content_by_url_mask() {
        val response = Unirest.get("http://127.0.0.1:8081/blabla/anymask/anyurl").asString()
        assertTrue(response.status == 404)
    }

    @Test
    @Throws(UnirestException::class)
    fun should_not_found_get_plaintext_with_content_by_url_mask2() {
        val response = Unirest.get("http://127.0.0.1:8081/anymas").asString()
        assertTrue(response.status == 404)
    }

    @Test
    @Throws(UnirestException::class)
    fun should_not_found_get_unmappedUrl() {
        val response = Unirest.get("http://127.0.0.1:8081/unexistedurl").asString()
        assertTrue(response.status == 404)
    }


    @Test
    @Throws(UnirestException::class)
    fun should_ok_get_json() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetJson")
                .asJson()

        assertTrue(response.headers["Content-Type"]!![0] == "application/json")
        assertTrue(response.status == 200)
        assertTrue(response.body.`object`.get("name") == "Korben Dallas")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_ok_post_json_with_headers() {

        val response = Unirest.post("http://127.0.0.1:8081/oauth/token")
                .asJson()

        assertTrue(response.headers["Content-Type"]!![0] == "application/json")
        assertTrue(response.status == 200)
        assertTrue(response.body.`object`.get("token_type") == "Bearer")
        assertTrue(response.headers["X-java-version"]!![0] == "1.8")
        assertTrue(response.headers["X-builder"]!![0] == "gradle")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_delete_text_plain_with_text_ok() {

        val response = Unirest.delete("http://127.0.0.1:8081/deleteResource")
                .asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertTrue(response.body == "OK")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_internal_error_patch() {

        val response = Unirest.patch("http://127.0.0.1:8081/patchResource")
                .asJson()

        assertTrue(response.headers["Content-Type"]!![0] == "application/json")
        assertTrue(response.status == 500)
        assertTrue(response.body.`object`.get("patchedParameter") == "patchedVariable with error")
    }

    @Test
    @Throws(UnirestException::class)
    fun should_ok_give_cookies() {

        val response = Unirest.get("http://127.0.0.1:8081/giveMeCookies")
                .asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertTrue(response.body == "Hello. Take your cookies")
        val cookieValues = response.headers["Set-Cookie"]!![0].split(";")
        assertTrue(cookieValues[0].split("=")[0] == "cookieName")
        assertTrue(cookieValues[0].split("=")[1] == "cookieValue")

        assertTrue(cookieValues[2].split("=")[0] == "Path")
        assertTrue(cookieValues[2].split("=")[1] == "/")

        assertTrue(cookieValues[3].split("=")[0] == "Domain")
        assertTrue(cookieValues[3].split("=")[1] == "127.0.0.1")

        assertTrue(cookieValues[5].split("=")[0] == "Max-Age")
        assertTrue(cookieValues[5].split("=")[1] == "3600")

    }

    @Test
    @Throws(Exception::class)
    fun should_ok_get_plaintext_with_content_on_second_secured_server() {
        val sslHttpClient = HttpClients.custom()
                .setSSLSocketFactory(SSLConnectionSocketFactory(SSLContexts.custom().
                        loadTrustMaterial(null, TrustSelfSignedStrategy()).build(), NullHostnameVerifier()))
                .build()

        Unirest.setHttpClient(sslHttpClient)

        val response = Unirest.get("https://127.0.0.1:8082/testGetText")
                .asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertTrue(response.body == "Content from the second server")
    }

    @Test
    @Throws(Exception::class)
    fun should_ok_get_plaintext_with_content_on_third_secured_server() {
        val sslHttpClient = HttpClients.custom()
                .setSSLSocketFactory(SSLConnectionSocketFactory(SSLContexts.custom().
                        loadTrustMaterial(null, TrustSelfSignedStrategy()).build(), NullHostnameVerifier()))
                .build()

        Unirest.setHttpClient(sslHttpClient)

        val response = Unirest.get("https://127.0.0.1:8083/testGetText")
                .asString()

        assertTrue(response.status == 404)
    }

    @Test
    fun testRoutingTable() {

        val routingTable: RoutingTable = RoutingTable()

        assertTrue(routingTable.getFullRouteMap().isEmpty())

        val beforeRouteHandler = object : BeforeRouteHandler {
            override fun handle(request: Request, response: Response) {}
        }
        val afterRouteHandler = object : AfterRouteHandler {
            override fun handle(request: Request, response: Response) {}
        }
        val routeHandler = object : RouteHandler {
            override fun handle(request: Request, response: Response) {}
        }

        routingTable.addRoute("/someRoute", HttpMethod.PUT, routeHandler, beforeRouteHandler, afterRouteHandler)
        routingTable.addRoute("/mask/*/newroute", HttpMethod.DELETE, routeHandler, beforeRouteHandler, afterRouteHandler)
        routingTable.addRoute("/newmask/*/routeagain/*/maskagain", HttpMethod.POST, routeHandler, beforeRouteHandler, afterRouteHandler)

        routingTable.find(HttpMethod.PUT, "/someRoute") ?: fail("It should not be null")
        routingTable.find(HttpMethod.DELETE, "/mask/sdfsdf/newroute") ?: fail("It should not be null")
        routingTable.find(HttpMethod.POST, "/newmask/ololo/routeagain/trololo/maskagain") ?: fail("It should not be null")
        assertTrue(routingTable.getFullRouteMap().size == 3)

        routingTable.deleteRoute("/someRoute", HttpMethod.PUT)
        routingTable.deleteRoute("/someRoute/*", HttpMethod.PUT)
        routingTable.deleteRoute("/someRoute*", HttpMethod.PUT)
        routingTable.deleteRoute("someRoute*", HttpMethod.PUT)
        routingTable.deleteRoute("/someRoute", HttpMethod.GET)
        assertTrue(routingTable.getFullRouteMap().size == 2)

        routingTable.deleteRoute("/mask/*/newroute", HttpMethod.DELETE)
        assertTrue(routingTable.getFullRouteMap().size == 1)

        routingTable.clearRoutes()
        assertTrue(routingTable.getFullRouteMap().isEmpty())

    }


    @Test
    @Throws(UnirestException::class)
    fun shouldGetSpringConfig() {

        val sslHttpClient = HttpClients.custom()
                .setSSLSocketFactory(SSLConnectionSocketFactory(SSLContexts.custom().
                        loadTrustMaterial(null, TrustSelfSignedStrategy()).build(), NullHostnameVerifier()))
                .build()

        Unirest.setHttpClient(sslHttpClient)

        val response = Unirest.get("https://127.0.0.1:8888/example-service/dev")
                .asJson()

        assertTrue(response.headers["Content-Type"]!![0] == "application/json")
        assertTrue(response.status == 200)

        assertTrue(response.body.`object`.get("name") == "example-service")

        val array = response.body.`object`.getJSONArray("profiles")
        assertTrue(array.get(0) == "dev")
        assertTrue(array.get(1) == "qa")

        val jsonObject = response.body
                .`object`
                .getJSONArray("propertySources")
                .getJSONObject(0)
                .getJSONObject("source")

        assertTrue(jsonObject.getInt("server.port") == 7100)
        assertTrue(jsonObject.getString("spring.datasource.url") == "\${POSTGRES_URL:jdbc:postgresql://localhost:5432/test_database}")

    }


}
