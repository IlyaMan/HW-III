package jmhtest

import org.openjdk.jmh.annotations.*
import kotlinx.coroutines.*
import kotlin.random.Random
import java.util.*
import kotlin.streams.asSequence

import Eight.singleThread
import Eight.prefixScan

import BigIntSum.prefixScan
import BigIntSum.singleThread

import Nine.inspect
import Nine.singleThread
import Ten.count


import java.util.concurrent.TimeUnit

@State(Scope.Thread)
open class CC{
    val x = Array<Int>(1000000, {it -> Random.nextInt() % 10})
    var y = Array<Int>(1000000, {it -> Random.nextInt() % 10})
    val source = "()"
    var sequence = Array<Pair<Int, Int>>(1000000, {it -> Pair(it, Random.nextInt() * 10)})
    var string = java.util.Random().ints(100000000, 0, source.length)
        .asSequence()
        .map(source::get)
        .joinToString("")
    var lens = Array<Double>(1000000, { it -> java.util.Random().nextDouble() * 100 })
    var angles = Array<Double>(1000000, { it -> java.util.Random().nextDouble() * 100 })



    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun BigIntSumSingleThread(){
        BigIntSum.singleThread(x, y)

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun BigIntSum_16() = runBlocking{
        var res = BigIntSum.prefixScan(x, y, 8)

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun BigIntSum_128() = runBlocking{
        var r = BigIntSum.prefixScan(x, y, 128)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun EightSingleThread(){
        var r = Eight.singleThread(sequence)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun Eight_16() = runBlocking{
        var res = Eight.prefixScan(sequence, 16)

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun Eight_128() = runBlocking{
        var res = Eight.prefixScan(sequence, 128)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun NineSingleThread(){
        var r = Nine.singleThread(string)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun Nine_16() = runBlocking{
        var res = Nine.inspect(string, 16)

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun Nine_128() = runBlocking{
        var res = Nine.inspect(string, 128)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)

    fun TenSingleThread(){
        Ten.singleThread(lens, angles)
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun Ten_16() = runBlocking{
        var res = Ten.count(lens, angles, 16)

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    fun Ten_128() = runBlocking{
        var res = Ten.count(lens, angles, 128)
    }


}

fun main(args : Array<String>){}