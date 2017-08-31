package ua.com.lavi.komock

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * Created by Oleksandr Loushkin
 */

object Waiter {

    fun untilNotEmpty(data: Collection<Any>, waitTime: Long = 1000) {
        val latch = CountDownLatch(1)
        thread {
            while (data.isEmpty()) {
            }
            latch.countDown()
        }
        latch.await(waitTime, TimeUnit.MILLISECONDS)
    }
}