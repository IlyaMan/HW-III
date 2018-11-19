import kotlinx.coroutines.*
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Lock

fun main(args : Array<String>){
    var counter : Int = 0;
    var l : Lock = ReentrantLock();

    GlobalScope.launch {
//        while (l.tryLock() == false) {println(l.tryLock())};

        l.lock()
        var a = counter;
        a++;
        counter = a;
        l.unlock();
    }

    GlobalScope.launch {
//        while (l.tryLock() == false) {println(l.tryLock())};

        l.lock()
        var a = counter;
        a++;
        counter = a;
        l.unlock();
    }

    Thread.sleep(100L)
    println("$counter")

}