package ua.com.lavi.komock.model

import org.eclipse.jetty.util.resource.Resource
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by Oleksandr Loushkin
 */

data class SslKeyStore(val keystoreResource: Resource,
                       val keystorePassword: String) {

    constructor(keystoreResource: String, keystorePassword: String)
            : this(ByteResource(Files.readAllBytes(Paths.get(keystoreResource))), keystorePassword)
}