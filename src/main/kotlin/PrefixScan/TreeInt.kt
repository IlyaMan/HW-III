package PrefixScan

import kotlinx.coroutines.*
import kotlin.random.Random

class Node(var value: Int?, var left: Node?, var right: Node?) {

    suspend fun generateTree(array: Array<Int>, l: Int, r: Int): Node {
        if (l == r) {
            return Node(array[l], null, null)
        }

        var lft = Node(null, null, null)
        var rght = Node(null, null, null)

            var t = GlobalScope.launch {
                var t = generateTree(array, l, (l + r) / 2)
                lft = t
            }
            var t1 = GlobalScope.launch {
                var t = generateTree(array, (l + r) / 2 + 1, r)
                rght = t
            }
        t.join()
        t1.join()

        return Node(null, lft, rght)
    }

}

fun generateSums(array: Array<Int>, threads: Int, sums: Array<Int>) = runBlocking {
    var jobs = arrayListOf<Job>()
    for (i in 0..threads - 1) {
        var job = GlobalScope.launch {
            var t = 0;
            if (i == threads - 1) {
                for (j in i * (array.size / threads)..array.size - 1) {
                    t += array[j];
                }
                sums[i] = t;
            } else {
                for (j in i * (array.size / threads)..(i + 1) * (array.size / threads) - 1) {
                    t += array[j];
                }
                sums[i] = t;
            }
        }
        jobs.add(job)
    }
    for (j in jobs) {
        j.join()
    }
}

suspend fun upswip(n: Node): Int {

    if (n.left == null) {
        return n.value!!

    }

    var t = GlobalScope.launch {
        n.value = upswip(n.left!!) + upswip(n.right!!)
    }
    t.join()

    return n.value!!
}

suspend fun downswip(n: Node): ArrayList<Int> {
    var result = arrayListOf<Int>()
    downswipInner(n, result)
    return result
}

suspend fun downswipInner(n: Node, result: ArrayList<Int>) {
    var t = GlobalScope.launch {
        if (n.left != null) {
            n.right!!.value = n.left!!.value!! + n.value!!
            n.left!!.value = n.value!!
            downswipInner(n.left!!, result)
            downswipInner(n.right!!, result)
        } else {
            result.add(n.value!!)
        }
    }
    t.join()
}

fun preDownswip(n: Node) {
    n.value = 0
}

suspend fun prefixSums(array: Array<Int>, prefixSums: Array<Int>) {
    var jobs = arrayListOf<Job>()
    for (i in 0..prefixSums.size - 1) {
        var t = GlobalScope.launch {
            var sum = 0;
            if (i == prefixSums.size - 1) {
                for (j in i * (array.size / prefixSums.size)..array.size - 1) {
                    var t = sum
                    sum += array[j]
                    array[j] += t + prefixSums[i]
                }
            } else {
                for (j in i * (array.size / prefixSums.size)..(i + 1) * (array.size / prefixSums.size) - 1) {
                    var t = sum
                    sum += array[j]
                    array[j] += t + prefixSums[i]
                }
            }

        }
        jobs.add(t)
    }
    for (j in jobs) {
        j.join()
    }
}

suspend fun prefixScan(array: Array<Int>, threads: Int): Array<Int> {
    var sums = Array<Int>(threads, { it -> 0 })
    generateSums(array, threads, sums)
    var h = Node(null, null, null).generateTree(sums, 0, sums.size - 1)
    var sum = upswip(h)
    preDownswip(h)
    var prefixSums = downswip(h).toTypedArray()
    prefixSums(array, prefixSums)
    return array
}

fun main(args: Array<String>) = runBlocking {
    test(1000, 1000, 16)
}

private fun singleThread(array: Array<Int>): Array<Int> {
    var t = 0
    for (i in 0..array.size - 1) {
        t += array[i]
        array[i] = t
    }
    return array
}

fun check(x: Array<Int>, y: Array<Int>): Boolean {
    if (x.size != y.size) return false
    for (i in 0..x.size - 1) {
        if (x[i] != y[i]) return false
    }
    return true
}

private suspend fun test(testRange: Int, arraySize: Int, threads: Int) {
    for (t in 1..threads) {
        for (i in 0..testRange) {
            var x = Array<Int>(arraySize, { it -> 0 })

            for (j in 1..arraySize - 1) {
                x[j] = Random.nextInt(0, 1000000)
            }
            var w1 = prefixScan(x, t)
            var w0 = singleThread(x)
            if (!check(w1, w0)) {
                println(false)
            }
        }
    }
}

fun observe(n: Node) {
    if (n.left == null) {
        println(n.value)
    } else {
        println(n.value)
        observe(n.left!!)
        observe(n.right!!)
    }

}