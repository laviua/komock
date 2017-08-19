package ua.com.lavi.komock.registrar

import java.nio.file.Path

/**
 * Created by Oleksandr Loushkin on 01.04.17.
 */
interface FileChangedHandler {
    fun onChange(filePath: Path)
}