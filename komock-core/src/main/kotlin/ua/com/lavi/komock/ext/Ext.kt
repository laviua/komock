package ua.com.lavi.komock.ext

import java.io.ByteArrayInputStream

fun java.lang.StringBuilder.toByteArrayInputStream(): ByteArrayInputStream {
    return ByteArrayInputStream(this.toString().toByteArray())
}