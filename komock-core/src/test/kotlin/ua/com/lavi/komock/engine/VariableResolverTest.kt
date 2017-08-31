package ua.com.lavi.komock.engine

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Oleksandr Loushkin on 01.07.17.
 */

class VariableResolverTest {

    @Test
    fun should_replace_two_parameters() {
        val responseBody = "testParameter is: \${first}. secondParameter: \${second}"
        val parametersMap: MutableMap<String, String> = HashMap()
        parametersMap["first"] = "abc"
        parametersMap["second"] = "xyz"

        val result = VariableResolver.resolve(parametersMap, responseBody)
        assertEquals(result, "testParameter is: abc. secondParameter: xyz")
    }

    @Test
    fun should_replace_one_parameter() {
        val responseBody = "\${first}"
        val parametersMap: MutableMap<String, String> = HashMap()
        parametersMap["first"] = "abc"
        parametersMap["second"] = "xyz"

        val result = VariableResolver.resolve(parametersMap, responseBody)
        assertEquals(result, "abc")
    }

    @Test
    fun should_not_replace_any_parameter() {
        val responseBody = "\${blablabla}"
        val parametersMap: MutableMap<String, String> = HashMap()
        parametersMap["first"] = "abc"
        parametersMap["second"] = "xyz"

        val result = VariableResolver.resolve(parametersMap, responseBody)
        assertEquals(result, "\${blablabla}")
    }

}