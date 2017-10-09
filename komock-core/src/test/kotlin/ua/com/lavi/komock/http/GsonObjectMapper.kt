package ua.com.lavi.komock.http

import com.google.gson.Gson
import com.mashape.unirest.http.ObjectMapper

/**
 * Created by Oleksandr Loushkin on 20.08.17.
 */

object GsonObjectMapper : ObjectMapper {
    private val gson = Gson()

    override fun writeValue(value: Any?): String {
        return gson.toJson(value)
    }

    override fun <T : Any?> readValue(value: String?, valueType: Class<T>?): T {
        return gson.fromJson(value, valueType)
    }
}
