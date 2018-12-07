package Ten

import kotlinx.coroutines.*
import java.lang.Math.*
import java.util.*

private class Node(var value: Position?, var left: Node?, var right: Node?) {

    suspend fun generateTree(array: Array<Position>, l: Int, r: Int): Node {
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

data class Position(var x: Double = 0.0, var y: Double = 0.0, var direction: Double = 0.0) {
    fun move(r: Double, angle: Double): Position {
        var ndirection = direction + toRadians(angle)
        var nx = x + r * cos(ndirection)
        var ny = y + r * sin(ndirection)
        return Position(nx, ny, ndirection)
    }

    fun move(p: Position): Position {
        var ndirection = direction + p.direction
        var nx = x + p.x * cos(direction) - p.y * sin(direction)
        var ny = y + p.x * sin(direction) + p.y * cos(direction)
        return Position(nx, ny, ndirection)
    }
}

private suspend fun getPositions(lens: Array<Double>, angles: Array<Double>, threads: Int): Array<Position> {
    var positions = Array<Position>(threads, { it -> Position() })
    var jobs = arrayListOf<Job>()

    for (i in 0..threads - 1) {
        var t = GlobalScope.launch {
            var l = i * (lens.size / threads)
            var r = 0

            if (i == threads - 1) {
                r = lens.size - 1
            } else {
                r = (i + 1) * (lens.size / threads) - 1
            }

            for (j in l..r) {
                positions[i] = positions[i].move(lens[j], angles[j])
            }
        }
        jobs.add(t)
    }
    for (j in jobs) {
        j.join()
    }
    return positions
}

private suspend fun upswip(n: Node): Position {
    if (n.left == null) {
        return n.value!!
    }

    var t = GlobalScope.launch {
        n.value = upswip(n.left!!).move(upswip(n.right!!))
    }
    t.join()

    return n.value!!
}

suspend fun count(lens: Array<Double>, angles: Array<Double>, threads: Int): Position {
    var t = getPositions(lens, angles, 4)
    var h = Node(null, null, null).generateTree(t, 0, t.size - 1)
    var res = upswip(h)
    return res
}

fun main(args: Array<String>) = runBlocking {
    test(1000, 10000, 8)
}

fun singleThread(lens: Array<Double>, angles: Array<Double>): Position {
    var t = Position()
    for (i in 0..lens.size - 1) {
        t = t.move(lens[i], angles[i])
    }
    return t
}

suspend private fun test(testRange: Int, commandsNum: Int, threads: Int) {
    for (j in 0..testRange) {
        var lens = Array<Double>(commandsNum, { it -> 0.0 })
        var angles = Array<Double>(commandsNum, { it -> 0.0 })
        for (i in 0..commandsNum - 1) {
            var l = Random().nextDouble() * 100
            var a = Random().nextDouble() * 100
            lens[i] = l
            angles[i] = a
        }
        var res = count(lens, angles, threads)
        var resS = singleThread(lens, angles)
        if (round(res.x) != round(resS.x) || round(res.y) != round(resS.y)) println(false)
//        else println("$res $resS")
    }

}
