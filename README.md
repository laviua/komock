# Komock [![Build Status](https://travis-ci.org/laviua/komock.svg?branch=master)](https://travis-ci.org/laviua/komock) [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
Here is Simple HTTP/Consul/SpringConfig mocker framework written in Kotlin.  

This tiny framework is very useful if you need to create REST based client for API service.  
We use this framework for develop microservices or API integration with third party systems.  

Main features:  
- Configurable response for any kind of HTTP request/response with wildcards, cookies and custom headers.
- SSL support
- Virtual Host support
- Ability to register in the Consul discovery service
- Ability to work like a simple spring config server

[Download latest standalone application](https://github.com/laviua/komock/releases/latest)

Maven sonatype repository:

    <dependency>
        <groupId>ua.com.lavi</groupId>
        <artifactId>komock-core</artifactId>
        <version>RELEASE</version>
    </dependency>

Look at self-describable configuration file (sample config exists in the komock-core root project directory):
**mock_example.yaml** 

How to run stand alone application:

1. Extract release version :
    
        komock-app-x.x.x.zip

3. Run

        bin\komock-app /path/your_config.yaml

HTTPS/SSL:

You can use your personal keystore. Just create it by the following command and set filename with password in the configuration file (secure section):

    keytool -genkey -alias replserver -keyalg RSA -keystore mock_keystore.jks -dname "CN=Mark Smith, OU=JavaSoft, O=Sun, L=Cupertino, S=California, C=US" -storepass mockpassword -keypass mockpassword

    secure:
      enabled: true
      keyStoreLocation: mock_keystore.jks
      keyStorePassword: mockpassword
