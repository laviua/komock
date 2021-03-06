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

**Simple http server**
https://github.com/laviua/komock/wiki/Simple-http-server-(Java)

**Oauth2 server**
https://github.com/laviua/komock/wiki/Oauth2-server

**Consul service registration**
https://github.com/laviua/komock/wiki/Consul
              
**Spring config server**
https://github.com/laviua/komock/wiki/Spring-config-server

**Unit testing**
https://github.com/laviua/komock/wiki/Unit-Testing-with-request-capturing-(Java)

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
