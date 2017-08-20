package ua.com.lavi.komock;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ua.com.lavi.komock.engine.handler.response.ResponseHandler;
import ua.com.lavi.komock.engine.model.HttpMethod;
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties;
import ua.com.lavi.komock.engine.server.handler.MockServer;
import ua.com.lavi.komock.engine.server.UnsecuredMockServer;
import ua.com.lavi.komock.model.odm.OdmRemoteApiResponse;
import ua.com.lavi.komock.model.odm.OdmRequest;
import ua.com.lavi.komock.model.odm.RiskCheckStatus;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Oleksandr Loushkin on 06.08.17.
 * ODM test example
 */
public class HttpRouterJavaConfigTest {

    private final String host = "localhost";
    private final int port = 9090;
    private static final String VERIFY_URL = "/verify";
    private static final String APPLICATION_JSON = "application/json";

    private static final String MERCHANT_CODE_PATH = "merchantCode";
    private static final String MERCHANT_CODE_SUCCESS_ODM01 = "ODM01";
    private static final String MERCHANT_CODE_REJECT_ODM02 = "ODM02";
    private static final String MERCHANT_CODE_NOT_FOUND_ODM03 = "ODM03";
    private static final String MERCHANT_CODE_INTERNAL_ERROR_ODM04 = "ODM04";
    private static final String MERCHANT_CODE_DELAY_1_SEC_ODM05 = "ODM05";
    private static final String MERCHANT_CODE_SERVER_UNAVAILABLE_ODM06 = "ODM06";
    private static final String MERCHANT_CODE_BAD_REQUEST_ODM07 = "ODM07";
    private static final String MERCHANT_CODE_CONFLICT_ODM08 = "ODM08";

    private final MockServer httpRouter = new UnsecuredMockServer(new HttpServerProperties()
            .withHost(host)
            .withPort(port));

    @Before
    public void setUp() {
        httpRouter.start();
        Unirest.setObjectMapper(GsonObjectMapper.INSTANCE);
        httpRouter.addRoute(VERIFY_URL, HttpMethod.POST, customResponseHandler());
    }

    @After
    public void tearDown() {
        httpRouter.stop();
    }

    @Test
    public void should_return_ok_approved() throws UnirestException {
        HttpResponse<OdmRemoteApiResponse> response = Unirest.post(String.format("http://%s:%s%s", host, port, VERIFY_URL))
                .body(odmRequest(MERCHANT_CODE_SUCCESS_ODM01)).asObject(OdmRemoteApiResponse.class);
        assertTrue(response.getHeaders().get("Content-Type").get(0).equals(APPLICATION_JSON));
        assertTrue(response.getStatus() == 200);
        assertEquals(RiskCheckStatus.APPROVED, response.getBody().getStatus());
    }

    @Test
    public void should_return_ok_rejected() throws UnirestException {
        HttpResponse<OdmRemoteApiResponse> response = Unirest.post(String.format("http://%s:%s%s", host, port, VERIFY_URL))
                .body(odmRequest(MERCHANT_CODE_REJECT_ODM02)).asObject(OdmRemoteApiResponse.class);
        assertTrue(response.getHeaders().get("Content-Type").get(0).equals(APPLICATION_JSON));
        assertTrue(response.getStatus() == 200);
        assertEquals(RiskCheckStatus.REJECTED, response.getBody().getStatus());
    }

    @Test
    public void should_return_not_found() throws UnirestException {
        HttpResponse<OdmRemoteApiResponse> response = Unirest.post(String.format("http://%s:%s%s", host, port, VERIFY_URL))
                .body(odmRequest(MERCHANT_CODE_NOT_FOUND_ODM03)).asObject(OdmRemoteApiResponse.class);
        assertTrue(response.getStatus() == 404);
        assertEquals(response.getBody(), null);
    }

    @Test
    public void should_return_internal_error() throws UnirestException {
        HttpResponse<OdmRemoteApiResponse> response = Unirest.post(String.format("http://%s:%s%s", host, port, VERIFY_URL))
                .body(odmRequest(MERCHANT_CODE_INTERNAL_ERROR_ODM04)).asObject(OdmRemoteApiResponse.class);
        assertTrue(response.getStatus() == 500);
        assertEquals(response.getBody(), null);
    }

    @Test
    public void should_return_ok_with_delay_1_sec() throws UnirestException {
        long startTime = System.currentTimeMillis();
        HttpResponse<OdmRemoteApiResponse> response = Unirest.post(String.format("http://%s:%s%s", host, port, VERIFY_URL))
                .body(odmRequest(MERCHANT_CODE_DELAY_1_SEC_ODM05)).asObject(OdmRemoteApiResponse.class);
        assertTrue(System.currentTimeMillis() - startTime >= 1000);
        assertTrue(response.getHeaders().get("Content-Type").get(0).equals(APPLICATION_JSON));
        assertTrue(response.getStatus() == 200);
        assertEquals(RiskCheckStatus.APPROVED, response.getBody().getStatus());
    }

    private ResponseHandler customResponseHandler() {
        return (request, response) -> {
            Object merchantCode = new JSONObject(request.requestBody()).get(MERCHANT_CODE_PATH);

            if (MERCHANT_CODE_SUCCESS_ODM01.equals(merchantCode)) {
                response.code(200);
                response.setContent("{\"uuid\":null,\"status\":\"APPROVED\",\"failedReason\":null}");
                response.contentType(APPLICATION_JSON);
            }
            if (MERCHANT_CODE_REJECT_ODM02.equals(merchantCode)) {
                response.code(200);
                response.setContent("{\"uuid\":null,\"status\":\"REJECTED\",\"failedReason\":null}");
                response.contentType(APPLICATION_JSON);
            }
            if (MERCHANT_CODE_NOT_FOUND_ODM03.equals(merchantCode)) {
                response.code(404);
            }
            if (MERCHANT_CODE_INTERNAL_ERROR_ODM04.equals(merchantCode)) {
                response.code(500);
            }
            if (MERCHANT_CODE_DELAY_1_SEC_ODM05.equals(merchantCode)) {
                try {
                    Thread.sleep(1000);
                    response.code(200);
                    response.setContent("{\"uuid\":null,\"status\":\"APPROVED\",\"failedReason\":null}");
                    response.contentType(APPLICATION_JSON);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            if (MERCHANT_CODE_SERVER_UNAVAILABLE_ODM06.equals(merchantCode)) {
                response.code(503);
            }
            if (MERCHANT_CODE_BAD_REQUEST_ODM07.equals(merchantCode)) {
                response.code(400);
                response.contentType(APPLICATION_JSON);
            }
            if (MERCHANT_CODE_CONFLICT_ODM08.equals(merchantCode)) {
                response.code(409);
            }
        };
    }

    private OdmRequest odmRequest(String merchantCode) {
        return new OdmRequest(UUID.randomUUID().toString(), merchantCode, BigDecimal.TEN, Currency.getInstance("EUR"));
    }
}
