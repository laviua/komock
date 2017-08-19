package ua.com.lavi.komock.registrar

import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Created by Oleksandr Loushkin on 01.04.17.
 */
class FileWatcher(private val fileChangedHandler: FileChangedHandler,
                  private val files: List<Path>,
                  private val period: Long) {

    private var threadName = this.javaClass.simpleName + "_" + System.currentTimeMillis()
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val timer = Timer(threadName, false)
    private val fileHashes: MutableMap<Path, String> = HashMap()

    fun start() {
        log.info("Start watching on files: $files with period: $period ms")

        timer.scheduleAtFixedRate(0, period) {
            for (file in files) {
                val currentHash = DigestUtils.md5Hex(String(Files.readAllBytes(file), Charsets.UTF_8))
                val oldHash = fileHashes[file]
                if (oldHash == null) { // first check
                    fileHashes.put(file, currentHash)
                } else { // next check
                    if (oldHash != currentHash) {
                        log.debug("File has been changed! $currentHash")
                        fileHashes.put(file, currentHash)
                        fileChangedHandler.onChange(file)
                    }
                }
            }
        }
    }
}