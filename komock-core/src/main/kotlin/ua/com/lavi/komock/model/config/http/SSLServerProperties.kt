package ua.com.lavi.komock.model.config.http

/**
 * Created by Oleksandr Loushkin
 */

open class SSLServerProperties {
    var enabled: Boolean = false
    var keyStoreLocation: String = "keystore.jks"
    var keyStorePassword: String = "password"
}