import java.util.concurrent.atomic.AtomicBoolean


import kotlinx.coroutines.*

var lock1 = AtomicBoolean(false)

fun main(args : Array<String>){

    var j = System.currentTimeMillis();
    GlobalScope.launch {
        var f = System.currentTimeMillis();
        lock1()
        var a = counter;
        a++;
        counter = a;
        unlock1();
        println(System.currentTimeMillis() - f);

    }
    var jj = System.currentTimeMillis();
    GlobalScope.launch {
        var f = System.currentTimeMillis();
        lock1()
        var a = counter;
        a++;
        counter = a;
        unlock1();
        println(System.currentTimeMillis() - f);
    }

    Thread.sleep(100L)
    println("$counter")

}


fun lock1(){
    while (lock1.getAndSet(true)) {
        // wait
    }
}
fun unlock1() {
    lock1.set(false);
}