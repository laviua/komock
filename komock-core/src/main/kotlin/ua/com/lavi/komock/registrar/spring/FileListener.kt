package ua.com.lavi.komock.registrar.spring

import java.nio.file.Path

/**
 * Created by Oleksandr Loushkin on 01.04.17.
 */
interface FileListener {
    fun onChange(filePath: Path)
}