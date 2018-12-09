import khttp.get
import org.jsoup.*
import org.jsoup.nodes.Document
import java.io.File
import java.util.concurrent.*
import kotlin.math.min


fun main(args: Array<String>) {
    var site: String = "http://hwproj.me/courses/28"
    downloadPages(site, 16)

}

fun downloadPages(link : String, threads : Int){
    var l = ConcurrentSkipListSet<String>()
    val executor = ThreadPoolExecutor(threads, threads, Long.MAX_VALUE, TimeUnit.MICROSECONDS, LinkedBlockingQueue())

    executor.execute({ treatLink(link, 0, 3, l, executor) })

    while (!executor.isTerminated) {}
}

fun treatLink(
    link: String,
    depth: Int,
    maxDepth: Int,
    list: ConcurrentSkipListSet<String>,
    threadPool: ThreadPoolExecutor
) {
    if (depth < maxDepth) {
        try {
            var page = Jsoup.connect(link).get()
            threadPool.execute({ saveLink(page, link, threadPool) })
            page.run {
                select("a").forEachIndexed { index, element ->
                    var url = element.attr("href")
                    if (url.length >= 1 && url[0] != '/' && url[0] != '#' && !list.contains(url)) {
                        list.add(url)
                        threadPool.execute({ treatLink(url, depth + 1, maxDepth, list, threadPool) })
                    }
                }
            }
        } catch (e: Exception) {
            if (e is org.jsoup.UnsupportedMimeTypeException || e is org.jsoup.HttpStatusException || e is javax.net.ssl.SSLHandshakeException || e is javax.net.ssl.SSLException || e is java.lang.IllegalArgumentException || e is java.net.UnknownHostException || e is java.net.SocketTimeoutException || e is java.net.MalformedURLException)
            else println(e)
        } finally {
            if (threadPool.activeCount == 1) {
                threadPool.shutdown()
            }

        }

    }
}

fun saveLink(page: Document, url: String, threadPool: ThreadPoolExecutor) {
    var f = File("res/${url.replace('/', '.').substring(0, min(url.length, 255))}").writeText(page.toString())
    if (threadPool.activeCount == 1) {
        threadPool.shutdown()
    }
}