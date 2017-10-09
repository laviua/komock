package ua.com.lavi.komock.http

import com.mashape.unirest.http.Unirest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ua.com.lavi.komock.http.handler.response.ResponseHandler
import ua.com.lavi.komock.model.HttpMethod
import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response
import ua.com.lavi.komock.model.config.http.HttpServerProperties
import ua.com.lavi.komock.http.server.MockServer
import ua.com.lavi.komock.http.server.UnsecuredMockServer

/**
 * Created by Oleksandr Loushkin
 */

class MockServerKotlinConfigTest {

    private val host = "localhost"
    private val port = 8090

    private val httpRouter: MockServer = UnsecuredMockServer(HttpServerProperties()
            .withHost(host)
            .withPort(port))

    @Before
    fun setUp() {
        httpRouter.start()
    }

    @After
    fun tearDown() {
        httpRouter.stop()
    }

    @Test
    fun should_run_kotlin_config() {

        httpRouter.addRoute("/testNoContent", HttpMethod.GET, customHandler())

        val response = Unirest.get("http://$host:$port/testNoContent").asString()

        assertTrue(response.status == 204)
        assertEquals(response.body, null)

    }

    private fun customHandler(): ResponseHandler {
        val responseHandler: ResponseHandler = object : ResponseHandler {
            override fun handle(request: Request, response: Response) {
                response.setCode(204)
                response.setContentType("text/plain")
            }
        }
        return responseHandler
    }
}