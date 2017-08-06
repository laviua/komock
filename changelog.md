# Change Log
All notable changes to this project will be documented in this file.

## [1.6.2] - 2017-08-06
### Changes
- Refactoring.
- Increase performance
- Improve builders for java code configuration
- Improve data structures
- Remove unused functionality
- Add gradle wrapper
- Add ktlint

## [1.6.1] - 2017-07-11
### Changes
- Add variables for log request/response
- Add request counter
- Add delay for response
- Add documentation and unit tests
- Remove kotlinx package
- Refactoring

## [1.6] - 2017-04-02
### Changes
- Reloadable spring config by period
- Callback for request
- Header based request authentication
- Response body substitution by request parameters

## [1.5] - 2017-03-10
### Changes
- Set variables in models are open. It can be override
- Add List of consul agents instead of the one consul agent
- Move server yaml property to httpServer
- Package name refactoring
- Update documentation and example

## [1.4] - 2017-03-09
### Changes
- Set models are open class
- Move ByteResource code to Kotlin
- Run only enabled routes
- Remove id from models

## [1.3] - 2017-03-02
### Changes
- ByteResource as SSL Key. Now you don't need to load ssl key from file by full path
- Add consul properties for checking by http, tcp, script
- Fixed log pattern with incorrect log date
- Fixed bug when virtual host initiated before router start
- Moved ArrayList to List in the API models
- Add id to the API models
- Add maven central badge. Add codeclimate check
