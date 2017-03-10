# Change Log
All notable changes to this project will be documented in this file.

## [1.5] - 2017-10-02
### Changes
- Set variables in models are open. It can be override.
- Add List of consul agents instead of the one consul agent.
- Move server yaml property to httpServer.
- Package name refactoring.
- Update documentation and example

## [1.4] - 2017-09-02
### Changes
- Set models are open class
- Move ByteResource code to Kotlin
- Run only enabled routes
- Remove id from models

## [1.3] - 2017-03-02
### Changes
- ByteResource as SSL Key. Now you don't need to load ssl key from file by full path.
- Add consul properties for checking by http, tcp, script.
- Fixed log pattern with incorrect log date.
- Fixed bug when virtual host initiated before router start.
- Moved ArrayList to List in the API models.
- Add id to the API models
- Add maven central badge. Add codeclimate check
