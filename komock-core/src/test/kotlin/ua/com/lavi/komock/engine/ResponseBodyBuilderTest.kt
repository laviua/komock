package ua.com.lavi.komock.engine

import org.junit.Test
import ua.com.lavi.komock.engine.handler.ResponseBodyHandler
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import kotlin.test.assertEquals

/**
 * Created by Oleksandr Loushkin on 01.07.17.
 */
class ResponseBodyBuilderTest {

    @Test
    fun should_replace_two_parameters() {
        val responseBody = "testParameter is: \${first}. secondParameter: \${second}"
        val requestHandlerBuilder = ResponseBodyHandler(RouteProperties())
        val parametersMap: MutableMap<String,String> = HashMap()
        parametersMap["first"] = "abc"
        parametersMap["second"] = "xyz"

        val result = requestHandlerBuilder.replacePlaceholders(parametersMap, responseBody)
        assertEquals(result, "testParameter is: abc. secondParameter: xyz")
    }

    @Test
    fun should_replace_one_parameter() {
        val responseBody = "\${first}"
        val requestHandlerBuilder = ResponseBodyHandler(RouteProperties())
        val parametersMap: MutableMap<String,String> = HashMap()
        parametersMap["first"] = "abc"
        parametersMap["second"] = "xyz"

        val result = requestHandlerBuilder.replacePlaceholders(parametersMap, responseBody)
        assertEquals(result, "abc")
    }

    @Test
    fun should_not_replace_any_parameter() {
        val responseBody = "\${blablabla}"
        val requestHandlerBuilder = ResponseBodyHandler(RouteProperties())
        val parametersMap: MutableMap<String,String> = HashMap()
        parametersMap["first"] = "abc"
        parametersMap["second"] = "xyz"

        val result = requestHandlerBuilder.replacePlaceholders(parametersMap, responseBody)
        assertEquals(result, "\${blablabla}")
    }

}