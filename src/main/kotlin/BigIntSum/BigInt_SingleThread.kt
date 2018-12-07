package BigIntSum

import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.random.Random

data class Carry(val carry: String) {
    fun binOperator(c: Carry): Carry {
        if (c.carry == "C" || c.carry == "N") return c
        else return this
    }
}

private class Node(var value: Carry?, var left: Node?, var right: Node?) {
    suspend fun generateTree(array: Array<Carry>, l: Int, r: Int): Node {
        if (l == r) {
            return Node(array[l], null, null)
        }

        var lft = Node(null, null, null)
        var rght = Node(null, null, null)


            var t1 = GlobalScope.launch {
                var t = generateTree(array, l, (l + r) / 2)
                lft = t
            }
            var t2 = GlobalScope.launch {
                var t = generateTree(array, (l + r) / 2 + 1, r)
                rght = t
            }
            t1.join()
            t2.join()


        return Node(null, lft, rght)
    }

}

private suspend fun upswip(n: Node): Carry {
    if (n.left == null) {
        return n.value!!
    }


    var t = GlobalScope.launch {
        n.value = upswip(n.left!!).binOperator(upswip(n.right!!))
    }

    t.join()

    return n.value!!
}

private suspend fun downswip(n: Node): ArrayList<Carry> {
    var result = arrayListOf<Carry>()
    downswipInner(n, result)
    return result
}

private suspend fun downswipInner(n: Node, result: ArrayList<Carry>) {
    var t = GlobalScope.launch {
        if (n.left != null) {
            n.right!!.value = n.value!!.binOperator(n.left!!.value!!)
            n.left!!.value = n.value!!
            downswipInner(n.left!!, result)
            downswipInner(n.right!!, result)
        } else {
            result.add(n.value!!)
        }
    }
    t.join()
}

private fun preDownswip(n: Node) {
    n.value = Carry("M")
}

private suspend fun prefixSums(array: Array<Carry>, prefixSums: Array<Carry>) {
    var jobs = arrayListOf<Job>()
    for (i in 0..prefixSums.size - 1) {
        var t = GlobalScope.launch {
            var sum = Carry("M");
            if (i == prefixSums.size - 1) {
                for (j in i * (array.size / prefixSums.size)..array.size - 1) {
                    var t = sum
                    sum = sum.binOperator(array[j])
                    array[j] = array[j].binOperator(t).binOperator(prefixSums[i])
                }
            } else {
                for (j in i * (array.size / prefixSums.size)..(i + 1) * (array.size / prefixSums.size) - 1) {
                    var t = sum
                    sum = sum.binOperator(array[j])
                    array[j] = array[j].binOperator(t).binOperator(prefixSums[i])
                }
            }

        }
        jobs.add(t)
    }
    for (j in jobs) {
        j.join()
    }
}

suspend fun sumUp(x: Array<Int>, y: Array<Int>, carrys: Array<Int>, threads: Int): Array<Int> {
    var jobs = arrayListOf<Job>()
    var res = Array<Int>(x.size, { it -> 0 })
    for (i in 0..threads - 1) {
        var t = GlobalScope.launch {
            if (i == threads - 1) {
                for (j in i * (x.size / threads)..x.size - 1) {
                    res[j] = (x[j] + y[j] + carrys[j]) % 10

                }

            } else {
                for (j in i * (x.size / threads)..(i + 1) * (x.size / threads) - 1) {
                    res[j] = (x[j] + y[j] + carrys[j]) % 10
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

suspend fun getCarrys(x: Array<Int>, y: Array<Int>, threads: Int): Array<Carry> {
    var jobs = arrayListOf<Job>()
    var carrys = Array(threads, { it -> Carry("M") })
    for (i in 0..threads - 1) {
        var t = GlobalScope.launch {
            if (i == threads - 1) {
                for (j in i * (x.size / threads)..x.size - 1) {
                    var t = x[j] + y[j]
                    if (t == 9) carrys[i] = carrys[i].binOperator(Carry("M"))
                    if (t < 9) carrys[i] = carrys[i].binOperator(Carry("N"))
                    if (t > 9) carrys[i] = carrys[i].binOperator(Carry("C"))
                }
            } else {
                for (j in i * (x.size / threads)..(i + 1) * (x.size / threads) - 1) {
                    var t = x[j] + y[j]
                    if (t == 9) carrys[i] = carrys[i].binOperator(Carry("M"))
                    if (t < 9) carrys[i] = carrys[i].binOperator(Carry("N"))
                    if (t > 9) carrys[i] = carrys[i].binOperator(Carry("C"))
                }
            }

        }
        jobs.add(t)
    }
    for (j in jobs) {
        j.join()
    }
    return carrys
}

suspend fun getCarrysFull(x: Array<Int>, y: Array<Int>, threads: Int): Array<Carry> {
    var jobs = arrayListOf<Job>()
    var carrys = Array(x.size, { it -> Carry("M") })
    for (i in 0..threads - 1) {
        var t = GlobalScope.launch {
            if (i == threads - 1) {
                for (j in i * (x.size / threads)..x.size - 1) {
                    var t = x[j] + y[j]
                    if (t == 9) carrys[j] = Carry("M")
                    if (t < 9) carrys[j] = Carry("N")
                    if (t > 9) carrys[j] = Carry("C")
                }
            } else {
                for (j in i * (x.size / threads)..(i + 1) * (x.size / threads) - 1) {
                    var t = x[j] + y[j]
                    if (t == 9) carrys[j] = Carry("M")
                    if (t < 9) carrys[j] = Carry("N")
                    if (t > 9) carrys[j] = Carry("C")
                }
            }

        }
        jobs.add(t)
    }
    for (j in jobs) {
        j.join()
    }
    return carrys
}

suspend fun setCarrys(initial: Array<Carry>, downswiped: Array<Carry>): Array<Int> {
    var jobs = arrayListOf<Job>()
    var res = Array<Int>(initial.size, { it -> 0 })
    var threads = downswiped.size
    for (i in 0..threads - 1) {
        var t = GlobalScope.launch {
            var t = Carry("M").binOperator(downswiped[i])
            if (i == threads - 1) {
                for (j in i * (initial.size / threads)..initial.size - 2) {
                    t = t.binOperator(initial[j])
                    when (t.carry) {
                        "C" -> res[j + 1] = 1
                        "N" -> res[j + 1] = 0
                    }
                }
            } else {
                for (j in i * (initial.size / threads)..(i + 1) * (initial.size / threads) - 1) {
                    t = t.binOperator(initial[j])
                    when (t.carry) {
                        "C" -> res[j + 1] = 1
                        "N" -> res[j + 1] = 0
                    }
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

suspend fun prefixScan(xx: Array<Int>, yy: Array<Int>, threads: Int): Array<Int> {

    var len = max(xx.size, yy.size)
    var x = stabilize(xx, len)
    var y = stabilize(yy, len)
    y.reverse()
    x.reverse()

    var fullCarrys = getCarrysFull(x, y, threads)
    var threadCarrys = getCarrys(x, y, threads)

    var h = Node(null, null, null).generateTree(threadCarrys, 0, threadCarrys.size - 1)
    upswip(h)

    preDownswip(h)
    var downswiped = downswip(h).toTypedArray()

    var finalCarrys = setCarrys(fullCarrys, downswiped)
    var res = sumUp(x, y, finalCarrys, threads)

    res.reverse()

    return res


}

fun main(args: Array<String>) = runBlocking {
    test(1000, 1000, 8)
}

private fun observe(n: Node) {
    if (n.left == null) {
        print("${n.value!!.carry}")
    } else {
//        println(n.value)
        observe(n.left!!)
        observe(n.right!!)
    }

}

fun singleThread(xx: Array<Int>, yy: Array<Int>): Array<Int> {
    var len = max(xx.size, yy.size)
    var x = stabilize(xx, len)
    var y = stabilize(yy, len)
    y.reverse()
    x.reverse()
    var res = Array<Int>(x.size, { it -> 0 })
    var c = 0
    for (i in 0..x.size - 1) {
        res[i] = ((x[i] + y[i] + c) % 10)
        if (x[i] + y[i] + c >= 10) {
            c = 1
        } else {
            c = 0
        }

    }
    res.reverse()
    return res
}

suspend private fun test(testRange: Int, intLen: Int, threads: Int) {
    for (t in 1..threads) {
        for (i in 0..testRange) {
            var x = Array<Int>(intLen, { it -> 0 })
            var y = Array<Int>(intLen, { it -> 0 })
            x[0] = 1
            y[0] = 1
            for (j in 1..intLen - 1) {
                x[j] = Random.nextInt(0, 9)
                y[j] = Random.nextInt(0, 9)
            }
            var w1 = prefixScan(x, y, t)
            var w0 = singleThread(x, y)
            if (!check(w1, w0)) {
                println(false)
            }
        }
    }
}

private fun check(x: Array<Int>, y: Array<Int>): Boolean {
    if (x.size != y.size) return false
    for (i in 0..x.size - 1) {
        if (x[i] != y[i]) return false
    }
    return true
}

private fun stabilize(x: Array<Int>, l: Int): Array<Int> {
    if (x.size < l) {
        var a = Array<Int>(l, { it -> 0 })
        for (i in (a.size - x.size)..(a.size - 1)) {
            a[i] = x[i - (a.size - x.size)]
        }
        return a
    }
    return x.copyOf()
}


