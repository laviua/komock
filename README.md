# Komock [![Build Status](https://travis-ci.org/laviua/komock.svg?branch=master)](https://travis-ci.org/laviua/komock) [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Maven Central](https://img.shields.io/maven-central/v/ua.com.lavi/komock-core.svg?style=plastic)]() [![Code Climate](https://codeclimate.com/github/laviua/komock/badges/gpa.svg)](https://codeclimate.com/github/laviua/komock)
Here is HTTP/Consul/SpringConfig mocker framework written in Kotlin.  

This tiny framework is very useful if you need to create REST based client for API service.  
We use this framework for develop microservices or API integration with third party systems.  

Main features:  
- Configurable response for any kind of HTTP request/response with wildcards, cookies and custom headers.
- SSL support
- Virtual Host support
- Consul discovery service support.
- Ability to work like a simple Spring Configuration Server
- Header based security
- Variables in the response by request parameters
- Callback support

## Download

Binaries are available at
https://github.com/laviua/komock/releases/latest

[Changelog](https://github.com/laviua/komock/blob/master/changelog.md)

## Maven Central repository

    <dependency>
        <groupId>ua.com.lavi</groupId>
        <artifactId>komock-core</artifactId>
        <version>RELEASE</version>
    </dependency>

## How to use

Run standalone application:

1. Extract release version from https://github.com/laviua/komock/releases/latest :
    
        komock-app-x.x.x.zip

3. Run

        bin\komock-app /path/your_config.yaml


## Examples
**Get Oauth2 token example**

    httpServers:
      -
        enabled: true
        name: auth-server
        port: 8080
        routes:
          -
            httpMethod: POST
            url: /oauth/token
            contentType: "application/json"
            responseBody: '{"access_token" : "ya29S6ZQbiBQpA5Rz8oty00xj-xydfdfddteerer-1eM",  "token_type" : "Bearer",  "expires_in" : 3600}'
            code: 200
          -
            httpMethod: GET
            url: /anymask/*/anypath/
            contentType: text/plain
            responseBody: Hello World. Test url mask with additional text
            code: 200

    curl -X POST "http://127.0.0.1:8080/oauth/token"
    {"access_token" : "ya29S6ZQbiBQpA5Rz8oty00xj-xydfdfddteerer-1eM",  "token_type" : "Bearer",  "expires_in" : 3600}

**Consul service**

    consulAgents:
        -
          enabled: false
          consulHost: 127.0.0.1
          consulPort: 8500
          services:
            -
              serviceId: customer-data-service
              serviceName: customer-data-service
              servicePort: 8081
              serviceAddress: 127.0.0.1
              checkInterval: 30s
              checkTimeout: 30s
              
**Spring config server**

    springConfig:
      enabled: true
      refreshPeriod: 10000
      sourceFolder: /somedirectory/configs/springconfig/
      profiles:
        - dev
        - qa
        - default
      httpServer:
        name: spring-config-server
        port: 8888
        host: 127.0.0.1
        ssl:
          enabled: true
          keyStoreLocation: mock_keystore.jks
          keyStorePassword: mockpassword

refreshPeriod: 0 - disable config watcher.

Http Server can be configured in Wiremock style for unit testing like this

**Unit testing**

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
    import ua.com.lavi.komock.engine.router.HttpRouter
    import ua.com.lavi.komock.engine.router.UnsecuredHttpRouter
    class HttpRouterKotlinConfigTest {

    private val host = "localhost"
    private val port = 9090

    private val httpRouter: HttpRouter = UnsecuredHttpRouter(HttpServerProperties()
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

        assertTrue(response.status == 200)
        assertEquals("blablabla", response.body)

    }

    @After
    fun tearDown() {
        httpRouter.stop()
    }

    private fun customHandler(): ResponseHandler {
        val responseHandler: ResponseHandler = object : ResponseHandler {
            override fun handle(request: Request, response: Response) {
                response.code(200)
                response.contentType("text/plain")
                response.content = "blablabla"
            }
        }
        return responseHandler
    }

    }

**Full config**

Callbacks, Mask patterns, cookies, virtualhosts, etc

Look at self-describable configuration file [Link](https://github.com/laviua/komock/blob/master/komock-core/mock_example.yml):

## HTTPS / SSL:

You can use your personal keystore. Just create it by the following command and set filename with password in the configuration file (secure section):

    keytool -genkey -alias replserver -keyalg RSA -keystore mock_keystore.jks -dname "CN=Mark Smith, OU=JavaSoft, O=Sun, L=Cupertino, S=California, C=US" -storepass mockpassword -keypass mockpassword

    ssl:
      enabled: true
      keyStoreLocation: mock_keystore.jks
      keyStorePassword: mockpassword

### License ###
Licensed under Apache 2 License
