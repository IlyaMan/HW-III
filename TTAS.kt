
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


var lock2 = AtomicBoolean(false)


fun main(args : Array<String>){

    var j = System.currentTimeMillis();
    GlobalScope.launch {
        var f = System.currentTimeMillis();
        lock2()
        var a = counter;
        a++;
        counter = a;
        unlock2();
        println(System.currentTimeMillis() - f);

    }
    var jj = System.currentTimeMillis();
    GlobalScope.launch {
        var f = System.currentTimeMillis();
        lock2()
        var a = counter;
        a++;
        counter = a;
        unlock2();
        println(System.currentTimeMillis() - f);
    }

    Thread.sleep(100L)
    println("$counter")

}


fun lock2() {
    while (true) {
        while (lock2.get()) {}
        if (!lock2.getAndSet(true)) { return }
    }
}

fun unlock2() {
    lock2.set(false)
}