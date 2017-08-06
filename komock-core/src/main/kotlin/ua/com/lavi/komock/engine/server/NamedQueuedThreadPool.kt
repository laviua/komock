package ua.com.lavi.komock.engine.server

import org.eclipse.jetty.util.thread.QueuedThreadPool

/**
 * Created by Oleksandr Loushkin on 10.07.2017.
 */
class NamedQueuedThreadPool(maxThreads: Int, minThreads: Int, idleTimeout: Int, threadName: String) :
        QueuedThreadPool(maxThreads, minThreads, idleTimeout) {

    init {
        name = threadName
    }
}