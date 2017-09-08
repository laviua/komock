package ua.com.lavi.komock.registrar

/**
 * Created by Oleksandr Loushkin on 09.09.17.
 */
interface Registrar<in T> {
    fun register(smtpServerProperties: T)
}