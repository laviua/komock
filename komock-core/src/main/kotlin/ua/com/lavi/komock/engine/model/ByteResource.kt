package ua.com.lavi.komock.engine.model

import org.eclipse.jetty.util.resource.Resource
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.channels.ReadableByteChannel

/**
 * Created by Oleksandr Loushkin
 */

class ByteResource(private val content: ByteArray) : Resource() {

    override fun isContainedIn(resource: Resource): Boolean {
        return false
    }

    override fun close() {
        // nothing to close
    }

    override fun exists(): Boolean {
        return true
    }

    override fun isDirectory(): Boolean {
        return false
    }

    override fun lastModified(): Long {
        return 0
    }

    override fun length(): Long {
        return 0
    }

    override fun getURL(): URL? {
        return null
    }

    override fun getFile(): File? {
        return null
    }

    override fun getName(): String? {
        return null
    }

    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(content)
    }

    override fun getReadableByteChannel(): ReadableByteChannel? {
        return null
    }

    override fun delete(): Boolean {
        return false
    }

    override fun renameTo(dest: Resource): Boolean {
        return false
    }

    override fun list(): Array<String> {
        return arrayOf("")
    }

    override fun addPath(path: String): Resource? {
        return null
    }
}
