import kotlinx.coroutines.*


var counter : Int = 0;
var want = arrayListOf<Boolean>(false, false);
var waiting = 0;

fun main(args : Array<String>){


    GlobalScope.launch {
        //        while (l.tryLock() == false) {println(l.tryLock())};

        lock(1)
        var a = counter;
        a++;
        counter = a;
        unlock(1);
    }

    GlobalScope.launch {
        //        while (l.tryLock() == false) {println(l.tryLock())};

        lock(2)
        var a = counter;
        a++;
        counter = a;
        unlock(2);
    }

    Thread.sleep(100L)
    println("$counter")

}


fun lock(id : Int){
    var other = 1 - id;
    want[id] = true;
    waiting = id;
    while (want[other] && waiting == id) {
        // wait
    }
}
fun unlock(id : Int) {
    want[id] = false;
}
