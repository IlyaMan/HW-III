package Nine

import PrefixScan.*

import kotlinx.coroutines.*
import java.util.*
import kotlin.streams.asSequence

private suspend fun replace(s: String, threads: Int): Array<Int> {
    var jobs = arrayListOf<Job>()
    var res = Array<Int>(s.length, { it -> 0 })
    for (i in 0..threads) {

        var l = i * (s.length / threads)
        var r = 0

        if (i == threads) {
            r = s.length - 1
        } else {
            r = (i + 1) * (s.length / threads) - 1
        }

        var t = GlobalScope.launch {
            for (j in l..r) {
                when (s[j]) {
                    '(' -> res[j] = 1
                    ')' -> res[j] = -1
                }
            }
        }
        jobs.add(t)
    }
    for (j in jobs) {
        j.join()
    }
    return res
}

suspend fun inspect(s: String, threads: Int): Boolean {
    var w = replace(s, threads)
    var sums = Array<Int>(threads, { it -> 0 })
    generateSums(w, threads, sums)
    var h = Node(null, null, null).generateTree(sums, 0, sums.size - 1)
    var sum = upswip(h)
    preDownswip(h)
    var prefixSums = downswip(h).toTypedArray()
    prefixSums(w, prefixSums)

    if (sum != 0) return false

    var jobs = arrayListOf<Job>()
    var flag = true
    for (i in 0..threads) {
        var t = GlobalScope.launch {
            var l = i * (w.size / threads)
            var r = 0

            if (i == threads) {
                r = w.size - 1
            } else {
                r = (i + 1) * (w.size / threads) - 1
            }
            for (j in l..r) {
                if (w[j] < 0) {
                    flag = false
                    return@launch
                }
            }
        }
        jobs.add(t)
    }
    for (j in jobs) {
        j.join()
    }
    return flag
}

fun main(args: Array<String>) = runBlocking {
    test(1000, 10, 2)
    test(1000, 10, 4)
    test(1000, 10, 10)
    test(1000, 10, 16)
    test(1000, 10, 32)
}

fun singleThread(s: String): Boolean {
    var stack = Stack<Int>()
    for (i in s) {
        when (i) {
            '(' -> stack.push(1)
            ')' -> {
                if (!stack.empty()) stack.pop()
                else return false
            }
        }
    }
    if (!stack.empty()) return false
    return true
}

suspend private fun test(testRange: Long, strLen: Long, threads: Int) {
    val source = "()"
    for (i in 0..testRange) {
        var s = Random().ints(strLen, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
        if (singleThread(s) != inspect(s, threads)) {
            println(s)
            println("$threads threads")
            println(singleThread(s))
            println(inspect(s, threads))
        }
    }
}

