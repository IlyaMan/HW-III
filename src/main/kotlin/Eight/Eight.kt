package Eight

import kotlinx.coroutines.*
import kotlin.random.Random


val sequence = arrayOf<Pair<Int, Int>>(Pair<Int, Int>(0, 1), Pair<Int, Int>(1, 2), Pair<Int, Int>(3, 4))


private class Node(var value: Pair<Int, Int>?, var left: Node?, var right: Node?) {

    suspend fun generateTree(array: Array<Pair<Int, Int>>, l: Int, r: Int): Node {
        if (l == r) {
            return Node(array[l], null, null)
        }

        var lft = Node(null, null, null)
        var rght = Node(null, null, null)

            var t = GlobalScope.launch {
                var t = generateTree(array, l, (l + r) / 2)
                lft = t
            }
            var t2 = GlobalScope.launch {
                var t = generateTree(array, (l + r) / 2 + 1, r)
                rght = t
            }
        t.join()
        t2.join()

        return Node(null, lft, rght)
    }

}

private fun generateSums(array: Array<Pair<Int, Int>>, threads: Int, sums: Array<Pair<Int, Int>>) = runBlocking {
    var jobs = arrayListOf<Job>()
    for (i in 0..threads - 1) {
        var job = GlobalScope.launch {
            var t = Pair(0, 0)
            if (i == threads - 1) {
                for (j in i * (array.size / threads)..array.size - 1) {
                    t = binOperator(t, array[j]);
                }
                sums[i] = t;
            } else {
                for (j in i * (array.size / threads)..(i + 1) * (array.size / threads) - 1) {
                    t = binOperator(t, array[j]);
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

private suspend fun upswip(n: Node): Pair<Int, Int> {
    if (n.left == null) {
        return n.value!!

    }
        var t = GlobalScope.launch {
            n.value = binOperator(upswip(n.left!!), upswip(n.right!!))
        }
    t.join()
    return n.value!!
}

private fun binOperator(f: Pair<Int, Int>, s: Pair<Int, Int>): Pair<Int, Int> {
    var newF = f.first * s.first
    var newS = s.first * f.second + s.second
    return Pair<Int, Int>(newF, newS)
}

suspend fun prefixScan(array: Array<Pair<Int, Int>>, threads: Int): Int {
    var sums = Array<Pair<Int, Int>>(threads, { it -> Pair(0, 0) })
    var x = generateSums(array, threads, sums)
    var h = Node(null, null, null).generateTree(array, 0, array.size - 1)
    var sum = upswip(h)
    return sum.second
}

fun main(args: Array<String>) = runBlocking {
    test(10000, 4)
}

fun singleThread(array: Array<Pair<Int, Int>>): Int {
    var sum = Pair(0, 0)
    for (i in array) {
        sum = binOperator(sum, i)
    }
    return sum.second
}

suspend private fun test(testRange: Int, threads: Int) {
    for (j in 0..testRange) {
        var list = arrayListOf<Pair<Int, Int>>(Pair(0, Random.nextInt(0, 10)))
        for (i in 0..testRange - 1) {
            list.add(Pair(Random.nextInt(0, 10), Random.nextInt(0, 10)))
        }
        var array = list.toTypedArray()

        var sum1 = prefixScan(sequence, threads)
        var sum2 = singleThread(sequence)

        if (sum1 == sum2) {

        } else {
            println("Error")
        }

    }


}