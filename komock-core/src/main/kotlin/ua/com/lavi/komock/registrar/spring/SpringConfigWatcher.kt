package ua.com.lavi.komock.registrar.spring

import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Created by Oleksandr Loushkin on 01.04.17.
 */
class SpringConfigWatcher {

    private val configHashes: MutableMap<Path, String> = HashMap()
    private var watchedFiles: List<Path> = ArrayList()
    private var fileListeners: List<FileListener> = ArrayList()
    private var period: Long = 10000

    private val log = LoggerFactory.getLogger(this.javaClass)

    val timer = Timer(this.javaClass.simpleName, false)

    fun start() {
        log.info("Start watching files: $watchedFiles with period: $period ms")
        timer.scheduleAtFixedRate(0, period) {
            checkChangedFiles()
        }
    }

    private fun checkChangedFiles() {
        for (springConfig in watchedFiles) {
            val lastHash = DigestUtils.md5Hex(String(Files.readAllBytes(springConfig), Charsets.UTF_8))
            val oldHash = configHashes[springConfig]
            if (oldHash == null) {
                configHashes.put(springConfig, lastHash)
            } else {
                if (oldHash != lastHash) {
                    log.debug("File has been changed! $lastHash")
                    configHashes.put(springConfig, lastHash)
                    fileListeners.forEach({ it.onChange(springConfig) })
                }
            }
        }
    }

    fun stop() {
        log.info("Stop watching files: $watchedFiles with period: $period ms")
        timer.cancel()
    }

    fun setListeners(fileListeners: List<FileListener>) {
        this.fileListeners = fileListeners
    }

    fun watchFiles(files: List<Path>, period: Long) {
        this.watchedFiles = files
        this.period = period
    }

}