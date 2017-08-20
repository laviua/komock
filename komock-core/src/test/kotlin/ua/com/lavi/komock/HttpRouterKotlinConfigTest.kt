package ua.com.lavi.komock

import com.mashape.unirest.http.Unirest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ua.com.lavi.komock.engine.handler.response.ResponseHandler
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.server.MockServer
import ua.com.lavi.komock.engine.server.UnsecuredMockServer

class HttpRouterKotlinConfigTest {

    private val host = "localhost"
    private val port = 9090

    private val httpRouter: MockServer = UnsecuredMockServer(HttpServerProperties()
            .withHost(host)
            .withPort(port))

    @Before
    fun setUp() {
        httpRouter.start()
    }

    @Test
    fun should_run_kotlin_config() {

        httpRouter.addRoute("/testNoContent", HttpMethod.GET, responseHandler = customHandler())

        val response = Unirest.get("http://$host:$port/testNoContent").asString()

        assertTrue(response.status == 204)
        assertEquals(response.body, null)

    }

    @After
    fun tearDown() {
        httpRouter.stop()
    }

    private fun customHandler(): ResponseHandler {
        val responseHandler: ResponseHandler = object : ResponseHandler {
            override fun handle(request: Request, response: Response) {
                response.code(204)
                response.contentType("text/plain")
            }
        }
        return responseHandler
    }
}