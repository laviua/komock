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
import ua.com.lavi.komock.engine.Router


/**
 * Created by Oleksandr Loushkin
 */

class SpringConfigTest {

    companion object {

        private const val MOCK_EXAMPLE_YAML = "mock_example.yml"

        @BeforeClass @JvmStatic
        fun startServer() {
            Application.main(arrayOf(MOCK_EXAMPLE_YAML))
        }

        @AfterClass @JvmStatic
        fun stopServer() {
            Router.stopAllRouters()
        }
    }

    @Test
    @Throws(UnirestException::class)
    fun shouldGetConfig() {

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
