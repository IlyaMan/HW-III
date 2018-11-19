import kotlinx.coroutines.*
fun main(args : Array<String>){
    var counter : Int = 0;
    GlobalScope.launch {
        var a = counter;
        a++;
        counter = a;

    }
    GlobalScope.launch {
        while (counter <= 4) {
            counter++;
        }
    }

    Thread.sleep(100L)
    println("$counter")

}