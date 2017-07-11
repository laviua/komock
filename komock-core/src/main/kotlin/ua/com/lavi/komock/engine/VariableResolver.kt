package ua.com.lavi.komock.engine

import java.util.regex.Pattern

/**
 * Replace input line by parameters map
 * Created by oleu on 10.07.2017.
 */
class VariableResolver {

    private val parameterRegexp = Pattern.compile("\\$\\{(.+?)}")

    fun resolve(parametersMap: Map<String, String>, inputLine: String): String {
        val matcher = parameterRegexp.matcher(inputLine)
        val sb = StringBuffer()
        while (matcher.find()) {
            val value = parametersMap[matcher.group(1)]
            if (value != null) {
                matcher.appendReplacement(sb, value)
            }
        }
        matcher.appendTail(sb)
        return sb.toString()
    }
}