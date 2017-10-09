package ua.com.lavi.komock.model.config.http

/**
 * Created by Oleksandr Loushkin on 20.08.17.
 */

open class CaptureProperties {
    var enabled: Boolean = false
    var bufferSize: Long = 10000

    fun enabled() : CaptureProperties {
        enabled = true
        return this
    }
}