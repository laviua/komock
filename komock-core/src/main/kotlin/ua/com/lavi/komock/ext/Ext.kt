package ua.com.lavi.komock.ext

import java.io.ByteArrayInputStream
import javax.mail.internet.MimeMessage

fun java.lang.StringBuilder.toByteArrayInputStream(): ByteArrayInputStream {
    return ByteArrayInputStream(this.toString().toByteArray())
}

fun MimeMessage.getFirstHeader(name: String): String {
    return this.getHeader(name)[0]
}