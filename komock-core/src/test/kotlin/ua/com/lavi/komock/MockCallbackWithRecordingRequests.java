package ua.com.lavi.komock;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ua.com.lavi.komock.engine.model.HttpMethod;
import ua.com.lavi.komock.engine.model.config.http.*;
import ua.com.lavi.komock.engine.server.MockServer;
import ua.com.lavi.komock.engine.server.UnsecuredMockServer;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Oleksandr Loushkin on 20.08.17.
 */
public class MockCallbackWithRecordingRequests {

    private static final String TEST_CALLBACK_BODY = "{'testCallbackBody': true}";
    private static final String TEST_RESPONSE_BODY = "{'testResponseBody': true}";
    private static final String TEST_REQUEST_VALUE = "testRequestValue";
    private static final String TEST_REQUEST_KEY = "testRequestKey";
    private static final String TEST_RESPONSE_VALUE = "testResponseValue";
    private static final String TEST_RESPONSE_KEY = "testResponseKey";
    private final String host = "localhost";
    private final int port_8080 = 8080;
    private final int port_8081 = 8081;
    private static final String VERIFY_URL = "/verify";
    private static final String APPLICATION_JSON = "application/json";

    private final MockServer firstServer = new UnsecuredMockServer(new HttpServerProperties()
            .withName("FirstServer")
            .withHost(host)
            .withPort(port_8080));

    private final MockServer captureServer = new UnsecuredMockServer(new HttpServerProperties()
            .withName("SecondServer")
            .withHost(host)
            .withPort(port_8081)
            .withCapture(new CaptureProperties().enabled())
    );

    @Before
    public void setUp() {
        firstServer.start();
        captureServer.start();
        Unirest.setObjectMapper(GsonObjectMapper.INSTANCE);

        firstServer.addRoute(routeProperties());
        captureServer.addRoute(callbackRouteProperties());
    }

    @After
    public void tearDown() {
        firstServer.stop();
        captureServer.stop();
    }

    @Test
    public void should_return_ok_with_captured_data() throws UnirestException, InterruptedException {
        HttpResponse<String> response = Unirest.post(String.format("http://%s:%s%s", host, port_8080, VERIFY_URL))
                .asString();
        assertTrue(response.getStatus() == 200);
        assertTrue(response.getHeaders().get("Content-Type").get(0).equals(APPLICATION_JSON));
        Thread.sleep(100L); //wait for callback
        List<CapturedData> capturedDataList = captureServer.getCapturedData();
        assertTrue(capturedDataList.size() == 1);
        CapturedData capturedData = capturedDataList.get(0);
        assertEquals(TEST_CALLBACK_BODY, capturedData.getRequestBody());
        assertEquals(TEST_REQUEST_VALUE, capturedData.getRequestHeaders().get(TEST_REQUEST_KEY));

        assertEquals(TEST_RESPONSE_BODY, capturedData.getResponseBody());
        assertEquals(TEST_RESPONSE_VALUE, capturedData.getResponseHeaders().get(TEST_RESPONSE_KEY));
    }

    private RouteProperties routeProperties() {
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setHttpMethod(HttpMethod.POST.name());
        routeProperties.setCode(200);
        routeProperties.setContentType(APPLICATION_JSON);
        routeProperties.setUrl(VERIFY_URL);

        routeProperties.setCallback(callbackProperties());
        return routeProperties;
    }

    private RouteProperties callbackRouteProperties() {
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setHttpMethod(HttpMethod.POST.name());
        routeProperties.setCode(200);
        routeProperties.setContentType(APPLICATION_JSON);
        routeProperties.setResponseBody(TEST_RESPONSE_BODY);
        routeProperties.setUrl(VERIFY_URL);
        routeProperties.setResponseHeaders(new HashMap<String, String>() {{
            put(TEST_RESPONSE_KEY, TEST_RESPONSE_VALUE);
        }});
        return routeProperties;
    }

    private CallbackProperties callbackProperties() {
        CallbackProperties callbackProperties = new CallbackProperties();
        callbackProperties.setEnabled(true);
        callbackProperties.setUrl(String.format("http://%s:%s%s", host, port_8081, VERIFY_URL));
        callbackProperties.setHttpMethod(HttpMethod.POST.name());
        callbackProperties.setRequestBody(TEST_CALLBACK_BODY);
        callbackProperties.setRequestHeaders(new HashMap<String, String>() {{
            put(TEST_REQUEST_KEY, TEST_REQUEST_VALUE);
        }});
        return callbackProperties;
    }
}
