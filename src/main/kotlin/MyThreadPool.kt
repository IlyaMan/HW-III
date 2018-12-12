import kotlinx.coroutines.*
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.atomic.*

var counter = 0
var counterLock = ReentrantLock()

class MyThreadPool(val threads: Int) {
    var q = MyLambdasQueue()
    var counter = AtomicInteger(0)

    suspend fun start(l : () -> Unit){
        q.append(l)
        var jobs = Array<Job?>(threads, {null})
        for (i in 0..threads - 1){
            var j = GlobalScope.launch {
                cycle()
            }
            jobs[i] = j
        }
        for (j in jobs){
            j!!.join()
        }

    }
    fun cycle() {
        while (true) {
            val l = q.take()
            counter.incrementAndGet()
            if (l != {}) {
                l.invoke();
                counter.decrementAndGet()
            } else counter.decrementAndGet()

            if (counter.compareAndSet(0, 0) && q.size == 0) break
        }
    }

    fun execute(t : () -> Unit){
        q.append(t)
    }
}