import khttp.get
import kotlinx.coroutines.runBlocking
import org.jsoup.*
import org.jsoup.nodes.Document
import java.io.File
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max
import kotlin.math.min


fun main(args: Array<String>) = runBlocking{
    val link: String = "https://ru.wikipedia.org/wiki/%D0%9F%D0%B0%D0%BB%D1%8C%D0%BC%D0%BE%D0%B2%D1%8B%D0%B9_%D1%87%D0%B5%D0%BA%D0%B0%D0%BD"
    println("1t")
    for (i in 0..10){
        var start = System.currentTimeMillis()
        downloadPages(link, 1, 1)
        println("${System.currentTimeMillis() - start}")
    }
    println("2t")
    for (i in 0..10){
        var start = System.currentTimeMillis()
        downloadPages(link, 1, 2)
        println("${System.currentTimeMillis() - start}")
    }
    println("4t")
    for (i in 0..10){
        var start = System.currentTimeMillis()
        downloadPages(link, 1, 4)
        println("${System.currentTimeMillis() - start}")
    }
    println("8t")
    for (i in 0..10){
        var start = System.currentTimeMillis()
        downloadPages(link, 1, 8)
        println("${System.currentTimeMillis() - start}")
    }
    println("16t")
    for (i in 0..10){
        var start = System.currentTimeMillis()
        downloadPages(link, 1, 16)
        println("${System.currentTimeMillis() - start}")
    }

//    start = System.currentTimeMillis()
//    downloadPages(link, 2, 128)
//    println()
//    println("128: ${System.currentTimeMillis() - start}")

}

suspend private fun downloadPages(link: String, maxDepth : Int, threads: Int) {
    var l = MyConcurrentList()
    var t = MyThreadPool(threads)
    t.start({treatLink(link, 0, maxDepth, l, t)})
}

private fun treatLink(link: String, depth: Int, maxDepth: Int, list: MyConcurrentList, threadPool: MyThreadPool) {
//    print("D")
    if (depth <= maxDepth) {
        try {
            var page = Jsoup.connect(link).get()
            threadPool.execute({ saveLink(page, link) })
            page.run {
                select("a").forEachIndexed { index, element ->
                    var url = element.attr("href")
                    if (url.length >= 1 && url[0] != '/' && url[0] != '#' && !list.contains(url)) {
                        list.add(url)
                        if (depth + 1 <= maxDepth) threadPool.execute({ treatLink(url, depth + 1, maxDepth, list, threadPool) })
                    }
                }
            }

        } catch (e: Exception) {
            if (e is org.jsoup.UnsupportedMimeTypeException || e is org.jsoup.HttpStatusException || e is javax.net.ssl.SSLHandshakeException || e is javax.net.ssl.SSLException || e is java.lang.IllegalArgumentException || e is java.net.UnknownHostException || e is java.net.SocketTimeoutException || e is java.net.MalformedURLException)
            else println(e)
        }
    }
}

private fun saveLink(page: Document, url: String) {
//    print("S")
    var f = File("res/${url.replace('/', '.').substring(0, min(url.length, 255))}").writeText(page.toString())

}
