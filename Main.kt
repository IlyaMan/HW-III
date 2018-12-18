import io.javalin.Javalin;
import io.javalin.core.util.Header
import io.javalin.staticfiles.Location
import io.javalin.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.commons.io.FileUtils
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import javax.imageio.ImageIO
import kotlin.math.min

val sessions = ConcurrentHashMap<WsSession, Pair<Channel<String>, String>>()
val sss = ConcurrentHashMap<String, WsSession>()

var threadPool = ThreadPoolExecutor(16, 16, Long.MAX_VALUE, TimeUnit.MICROSECONDS, LinkedBlockingQueue())

fun server(){
    var app = Javalin.create().disableStartupBanner().enableCorsForOrigin("*").apply {
        ws("/") { ws ->
            ws.onConnect { session ->
                val username = UUID.randomUUID().toString()
                sessions.put(session, Pair(Channel<String>(), username))
                sss.put(username, session)
                session.send("Hello")
                GlobalScope.launch {
                    val s = session
                    while (true){
                        var m = sessions[s]!!.first.receive()
                        handleMessage(m, s, sessions)
                    }
                }
            }
            ws.onClose { session, status, message ->
                val username = sessions[session]
                sessions.remove(session)
            }
            ws.onMessage { session, message ->
                runBlocking {
                    sessions[session]!!.first.send(message)
                }
                println(message)
            }
        }
        post("/post"){ ctx ->
            println(ctx)
            ctx.header(Header.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
            var filter = ctx.formParam("filter")
            var id = ctx.formParam("id")
            ctx.uploadedFiles("files").forEach { (contentType, content, name, extension) ->
                FileUtils.copyInputStreamToFile(content, File("results/" + id + "." + "jpg"))
            }
            threadPool.execute({handleMessage(filter!!, sss[id]!!, sessions)})

        }
    }
    app.enableStaticFiles("./results", Location.EXTERNAL)
    app.start(8080)
}


fun main(args : Array<String>){
    System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
    System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
    server()

}


fun handleMessage(message : String, s : WsSession, sessions : ConcurrentHashMap<WsSession, Pair<Channel<String>, String>>){
    when (message){
        "getFilters" -> {
            s.send("Filters " + "Blur_3" + " " + "Blur_10" + " " + "Blur_30")
        }
        "getId" -> {
            s.send("Id " + sessions[s]!!.second)
        }
        "Blur_3" -> {
            val image = ImageIO.read(File("results/" + sessions[s]!!.second + ".jpg"));
            println("results/" + sessions[s]!!.second + ".jpg")
            var ni = BufferedImage(image.width - 12, image.height - 6, image.type);
            blurRows(3, 16, image, ni, s)
            ImageIO.write(ni, "jpg", File("results/" + sessions[s]!!.second + ".jpg"));
            s.send("ready")
        }
        "Blur_10" -> {
            val r = 10
            val image = ImageIO.read(File("results/" + sessions[s]!!.second + ".jpg"));
            println("results/" + sessions[s]!!.second + ".jpg")
            var ni = BufferedImage(((image.width/16)*16 - 2*r), image.height - 2*r, image.type);
            val pixels = getPixels(image);
            blurRows(10, 16, image, ni, s)
            ImageIO.write(ni, "jpg", File("results/" + sessions[s]!!.second + ".jpg"));
            s.send("ready")

        }
        "Blur_30" -> {
            val image = ImageIO.read(File("results/" + sessions[s]!!.second + ".jpg"));
            println("results/" + sessions[s]!!.second + ".jpg")
            var ni = BufferedImage(image.width - 60, image.height - 60, image.type);
            val pixels = getPixels(image);
            blurRows(30, 16, image, ni, s)
            ImageIO.write(ni, "jpg", File("results/" + sessions[s]!!.second + ".jpg"));
            s.send("ready")
        }

    }
}