import java.util.concurrent.locks.ReentrantLock

class MyConcurrentList{
    private var list = arrayListOf<String>()
    private var lock = ReentrantLock()

    fun contains(s: String): Boolean {
//        lock.lock()
        var res = list.contains(s)
//        lock.unlock()
        return res
    }

    fun add(s: String) {
        lock.lock()
        list.add(s)
        lock.unlock()
    }
}


class MyLambdasQueue(){
    var list = arrayListOf<()->Unit>()
    var lock = ReentrantLock()

    var size : Int = 0
    get(){
        lock.lock()
        var s = list.size
        lock.unlock()
        return s
    }

    fun append(l : ()->Unit){
        lock.lock()
        list.add(l)
        lock.unlock()
    }
    fun take() : ()->Unit {
        var res : () -> Unit
        lock.lock()
        if (list.size == 0) res = {}
        else {
            res = list.get(0)
            list.remove(res)
        }
        lock.unlock()
        return res
    }


}