package ua.com.lavi.komock

import com.mashape.unirest.http.Unirest
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.yaml.snakeyaml.Yaml
import ua.com.lavi.komock.engine.RoutingTable
import ua.com.lavi.komock.engine.handler.AfterRequestHandler
import ua.com.lavi.komock.engine.handler.BeforeRequestHandler
import ua.com.lavi.komock.engine.handler.CallbackHandler
import ua.com.lavi.komock.engine.handler.RequestHandler
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.KomockConfiguration
import ua.com.lavi.komock.registrar.ServerRegistrar
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
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
                KomockRunner().run(Yaml().loadAs<KomockConfiguration>(it, KomockConfiguration::class.java))
            }
        }

        @AfterClass @JvmStatic
        fun stopServer() {
            ServerRegistrar.stopAllServers()
        }
    }

    @Test
    fun should_ok_testCallback() {

        val response = Unirest.get("http://127.0.0.1:8081/testcallback")
                .asJson()

        assertTrue(response.headers["Content-Type"]!![0] == "application/json")
        assertTrue(response.status == 200)
    }

    @Test
    fun should_ok_get_noContent() {

        val response = Unirest.get("http://127.0.0.1:8081/testNoConent").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 204)
        assertEquals(response.body, null)
    }

    @Test
    fun should_ok_get_plaintext_with_content() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetText").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Hello World. Plain Text.")
    }

    @Test
    fun should_ok_get_plaintext_with_content_by_parameters() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetTextWithParameters?testP=blabla&someElse=abc").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Here is the parameter blabla and other abc")
    }

    @Test
    fun should_ok_get_plaintext_with_content_by_parameters_empty_request() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetTextWithParameters").asString()
        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Here is the parameter \${testP} and other \${someElse}")
    }

    @Test
    fun should_ok_get_secured_area() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetTextSecuredRoute")
                .header("X-TOKEN-HEADER", "69b30db6-1238-11e7-93ae-92361f002671")
                .asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Hello World. This is a header based secured area")
    }

    @Test
    fun should_forbidden_get_secured_area() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetTextSecuredRoute")
                .asString()

        assertTrue(response.status == 401)
        assertEquals(response.body, "")
    }

    @Test
    fun should_ok_post_plaintext_with_content() {

        val response = Unirest.post("http://127.0.0.1:8081/testGetText").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Hello World Again. Plain Text.")
    }

    @Test
    fun should_ok_get_plaintext_with_content_by_url_mask_and_additional_text() {

        val response = Unirest.get("http://127.0.0.1:8081/anymask/blablabla/anypath/").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Hello World. Test url mask with additional text")
    }

    @Test
    fun should_ok_get_plaintext_with_content_by_url_mask_and_additional_text2() {

        val response = Unirest.get("http://127.0.0.1:8081/somemask/xxx/somepath/yyy/somepath").asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Some Complicated Path")
    }

    @Test
    fun should_not_ok_get_plaintext_with_content_by_url_mask_and_additional_text2() {
        val response = Unirest.get("http://127.0.0.1:8081/somemas/xxx/somepath/yyy/somepath").asString()
        assertTrue(response.status == 404)
    }


    @Test
    fun should_not_found_get_plaintext_with_content_by_url_mask() {
        val response = Unirest.get("http://127.0.0.1:8081/blabla/anymask/anyurl").asString()
        assertTrue(response.status == 404)
    }

    @Test
    fun should_not_found_get_plaintext_with_content_by_url_mask2() {
        val response = Unirest.get("http://127.0.0.1:8081/anymas").asString()
        assertTrue(response.status == 404)
    }

    @Test
    fun should_not_found_get_unmappedUrl() {
        val response = Unirest.get("http://127.0.0.1:8081/unexistedurl").asString()
        assertTrue(response.status == 404)
    }


    @Test
    fun should_ok_get_json() {

        val response = Unirest.get("http://127.0.0.1:8081/testGetJson")
                .asJson()

        assertTrue(response.headers["Content-Type"]!![0] == "application/json")
        assertTrue(response.status == 200)
        assertTrue(response.body.`object`.get("name") == "Korben Dallas")
    }

    @Test
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
    fun should_delete_text_plain_with_text_ok() {

        val response = Unirest.delete("http://127.0.0.1:8081/deleteResource")
                .asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "OK")
    }

    @Test
    fun should_internal_error_patch() {

        val response = Unirest.patch("http://127.0.0.1:8081/patchResource")
                .asJson()

        assertTrue(response.headers["Content-Type"]!![0] == "application/json")
        assertTrue(response.status == 500)
        assertTrue(response.body.`object`.get("patchedParameter") == "patchedVariable with error")
    }

    @Test
    fun should_ok_give_cookies() {

        val response = Unirest.get("http://127.0.0.1:8081/giveMeCookies")
                .asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Hello. Take your cookies")
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
    fun should_ok_get_plaintext_with_content_on_second_secured_server() {
        val sslHttpClient = HttpClients.custom()
                .setSSLContext(SSLContextBuilder()
                        .loadTrustMaterial(null) { _, _ -> true }.build())
                .setSSLHostnameVerifier(NoopHostnameVerifier())
                .build()

        Unirest.setHttpClient(sslHttpClient)

        val response = Unirest.get("https://127.0.0.1:8082/testGetText")
                .asString()

        assertTrue(response.headers["Content-Type"]!![0] == "text/plain")
        assertTrue(response.status == 200)
        assertEquals(response.body, "Content from the second server")
    }

    @Test
    fun should_ok_get_plaintext_with_content_on_third_secured_server() {
        val sslHttpClient = HttpClients.custom()
                .setSSLContext(SSLContextBuilder()
                        .loadTrustMaterial(null) { _, _ -> true }.build())
                .setSSLHostnameVerifier(NoopHostnameVerifier())
                .build()

        Unirest.setHttpClient(sslHttpClient)

        val response = Unirest.get("https://127.0.0.1:8083/testGetText")
                .asString()

        assertTrue(response.status == 404)
    }

    @Test
    fun should_ok_delay_at_least_time() {
        val start = System.currentTimeMillis()
        val response = Unirest.get("http://127.0.0.1:8081/delayTest").asString()
        val execTime = System.currentTimeMillis() - start
        assertTrue(response.status == 200)
        assertTrue(execTime >= 1000)
    }

    @Test
    fun testRoutingTable() {

        val routingTable: RoutingTable = RoutingTable()

        assertTrue(routingTable.getFullRouteMap().isEmpty())

        val beforeRouteHandler = object : BeforeRequestHandler {
            override fun handle(request: Request, response: Response) {}
        }
        val afterRouteHandler = object : AfterRequestHandler {
            override fun handle(request: Request, response: Response) {}
        }
        val routeHandler = object : RequestHandler {
            override fun handle(request: Request, response: Response) {}
        }

        val callbackHandler = object : CallbackHandler {
            override fun handle(request: Request, response: Response) {}
        }

        routingTable.addRoute("/someRoute", HttpMethod.PUT, routeHandler, beforeRouteHandler, afterRouteHandler, callbackHandler)
        routingTable.addRoute("/mask/*/newroute", HttpMethod.DELETE, routeHandler, beforeRouteHandler, afterRouteHandler, callbackHandler)
        routingTable.addRoute("/newmask/*/routeagain/*/maskagain", HttpMethod.POST, routeHandler, beforeRouteHandler, afterRouteHandler, callbackHandler)
        assertTrue(routingTable.getFullRouteMap().size == 3)

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
    fun shouldGetSpringConfig() {

        val sslHttpClient = HttpClients.custom()
                .setSSLContext(SSLContextBuilder()
                        .loadTrustMaterial(null) { _, _ -> true }.build())
                .setSSLHostnameVerifier(NoopHostnameVerifier())
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
        assertTrue(array.get(2) == "default")

        val jsonObject = response.body
                .`object`
                .getJSONArray("propertySources")
                .getJSONObject(0)
                .getJSONObject("source")

        assertTrue(jsonObject.getInt("server.port") == 7100)
        assertTrue(jsonObject.getString("spring.datasource.url") == "\${POSTGRES_URL:jdbc:postgresql://localhost:5432/test_database}")

    }


}
