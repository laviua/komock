package ua.com.lavi.komock.engine.model

import org.eclipse.jetty.util.resource.Resource
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.nio.channels.ReadableByteChannel

/**
 * Created by Oleksandr Loushkin
 */

class ByteResource(private val content: ByteArray) : Resource() {

    @Throws(MalformedURLException::class)
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

    @Throws(IOException::class)
    override fun getFile(): File? {
        return null
    }

    override fun getName(): String? {
        return null
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(content)
    }

    @Throws(IOException::class)
    override fun getReadableByteChannel(): ReadableByteChannel? {
        return null
    }

    @Throws(SecurityException::class)
    override fun delete(): Boolean {
        return false
    }

    @Throws(SecurityException::class)
    override fun renameTo(dest: Resource): Boolean {
        return false
    }

    override fun list(): Array<String> {
        return arrayOf("")
    }

    @Throws(IOException::class)
    override fun addPath(path: String): Resource? {
        return null
    }
}
