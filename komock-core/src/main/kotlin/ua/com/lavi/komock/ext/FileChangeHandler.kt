package ua.com.lavi.komock.ext

import java.nio.file.Path

/**
 * Created by Oleksandr Loushkin on 01.04.17.
 */

interface FileChangeHandler {
    fun onFileChange(filePath: java.nio.file.Path)
}