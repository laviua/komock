package ua.com.lavi.komock.engine.model

import org.eclipse.jetty.util.resource.Resource

/**
 * Created by Oleksandr Loushkin
 */

data class SslKeyStore(val keystoreResource: Resource,
                       val keystorePassword: String)