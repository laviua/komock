package ua.com.lavi.komock;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ua.com.lavi.komock.engine.model.HttpMethod;
import ua.com.lavi.komock.engine.model.config.http.CallbackProperties;
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties;
import ua.com.lavi.komock.engine.model.config.http.RouteProperties;
import ua.com.lavi.komock.engine.server.MockServer;
import ua.com.lavi.komock.engine.server.UnsecuredMockServer;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by Oleksandr Loushkin on 20.08.17.
 */
public class MockCallbackWithRecordingRequests {

    private final String host = "localhost";
    private final int port_8080 = 8080;
    private final int port_8081 = 8081;
    private static final String VERIFY_URL = "/verify";
    private static final String APPLICATION_JSON = "application/json";

    private final MockServer firstServer = new UnsecuredMockServer(new HttpServerProperties()
            .withName("FirstServer")
            .withHost(host)
            .withPort(port_8080));

    private final MockServer secondServer = new UnsecuredMockServer(new HttpServerProperties()
            .withName("SecondServer")
            .withHost(host)
            .withPort(port_8081));

    @Before
    public void setUp() {
        firstServer.start();
        secondServer.start();
        Unirest.setObjectMapper(GsonObjectMapper.INSTANCE);

        firstServer.addRoute(routeProperties1());
        secondServer.addRoute(routeProperties2());
    }

    @After
    public void tearDown() {
        firstServer.stop();
        secondServer.stop();
    }

    @Test
    public void should_return_ok_approved() throws UnirestException {
        HttpResponse<String> response = Unirest.post(String.format("http://%s:%s%s", host, port_8080, VERIFY_URL)).asString();
        assertTrue(response.getStatus() == 200);
        assertTrue(response.getHeaders().get("Content-Type").get(0).equals(APPLICATION_JSON));

    }

    private RouteProperties routeProperties1() {
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setHttpMethod(HttpMethod.POST.name());
        routeProperties.setCode(200);
        routeProperties.setContentType(APPLICATION_JSON);
        routeProperties.setUrl(VERIFY_URL);

        routeProperties.setCallback(callbackProperties());
        return routeProperties;
    }

    private RouteProperties routeProperties2() {
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setHttpMethod(HttpMethod.POST.name());
        routeProperties.setCode(200);
        routeProperties.setContentType(APPLICATION_JSON);
        routeProperties.setUrl(VERIFY_URL);
        return routeProperties;
    }

    private CallbackProperties callbackProperties() {
        CallbackProperties callbackProperties = new CallbackProperties();
        callbackProperties.setEnabled(true);
        callbackProperties.setUrl(String.format("http://%s:%s%s", host, port_8081, VERIFY_URL));
        callbackProperties.setHttpMethod(HttpMethod.POST.name());
        callbackProperties.setRequestBody("testCallbackBody");
        callbackProperties.setRequestHeaders(new HashMap<String, String>() {{
            put("testKey","testValue");
        }});
        return callbackProperties;
    }
}
