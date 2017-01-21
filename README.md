# Komock [![Build Status](https://travis-ci.org/laviua/komock.svg?branch=master)](https://travis-ci.org/laviua/komock) [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
Here is Simple HTTP/Consul/SpringConfig mocker framework written in Kotlin.  

This tiny framework is very useful if you need to create REST based client for API service.  
We use this framework for develop microservices or API integration with third party systems.  

Main features:  
- Configurable response for any kind of HTTP request/response with wildcards, cookies and custom headers.
- SSL support
- Ability to register in the Consul discovery service
- Ability to work like a simple spring config server


Look at self-describable configuration file:
**mock_example.yaml** 

1.make distribution:

    gradle clean build

2.extract distribution from the following path:
    
    build/distributions/komock-xx-version.zip

3.run

    bin\komock d:\mock_example.yaml

HTTPS/SSL:

You can use your personal keystore. Just create it and put into the config:

    keytool -genkey -alias replserver -keyalg RSA -keystore mock_keystore.jks -dname "CN=Mark Smith, OU=JavaSoft, O=Sun, L=Cupertino, S=California, C=US" -storepass mockpassword -keypass mockpassword
