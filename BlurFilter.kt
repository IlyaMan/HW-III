import io.javalin.websocket.WsSession
import kotlinx.coroutines.*
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val r = 3;
    val image = ImageIO.read(File("./src/main/resources/4k.jpeg"));
    var ni = BufferedImage(image.width, image.height, image.type);
    val pixels = getPixels(image);
    var startTime = System.currentTimeMillis();
    blurColumns(r, 8, image, ni);
    println("${System.currentTimeMillis() - startTime}");
    val t = ImageIO.write(ni, "jpg", File("./src/main/resources/test1.jpg"));
}


fun blurPixels(r : Int, lines : Int, image : BufferedImage, ni : BufferedImage) = runBlocking {
    val pixels = getPixels(image);
    var jobs = arrayListOf<Job>();
    for (x in r..image.width - (r + 1)){
        for (y in r..image.height - (r + 1)){
            val job = GlobalScope.launch { // launch new coroutine and keep a reference to its Job
                var medium = findMedium(x, y, pixels, r);
                ni.setRGB(x, y, medium);
            }
            jobs.add(job);

        }
    }
    for (j in jobs){
        j.join()
    }

}

fun blurBasic(r : Int, lines : Int, image : BufferedImage, ni : BufferedImage) = runBlocking {
    val pixels = getPixels(image);
    for (x in r..image.width - (r + 1)){
        for (y in r..image.height - (r + 1)){
            var medium = findMedium(x, y, pixels, r);
            ni.setRGB(x, y, medium);
        }
    }
}

fun blurRows(r : Int, lines : Int, image : BufferedImage, ni : BufferedImage, s : WsSession) = runBlocking {
    var counter = AtomicInteger(0)
    val pixels = getPixels(image);
    var jobs = arrayListOf<Job>();
    val st = (image.width - 2*r)/lines;
    val startTime = System.currentTimeMillis()
    for (i in 0..lines - 1){
        val job = GlobalScope.launch {
            var startTime = System.currentTimeMillis();
            for (x in (r + st * i)..(r + st * (i + 1))){
                for (y in r..image.height - (r + 1)){
                    var medium = findMedium(x, y, pixels, r);
                    ni.setRGB(x - r, y - r, medium);
                    counter.incrementAndGet()
                }
            }
            if (System.currentTimeMillis() - startTime >= 100){
                s.send("counter " + "${counter.incrementAndGet().toFloat() / (image.height - 4*r)/ (image.width - 4*r)}")
                startTime = System.currentTimeMillis()
            }
        }
        jobs.add(job);
    }
    jobs.forEach({j -> j.join()})

    s.send("counter " + "${counter.incrementAndGet() / (image.height - 4*r)/ (image.width - 4*r)}")
}

fun blurColumns(r : Int, lines : Int, image : BufferedImage, ni : BufferedImage) = runBlocking {
    val pixels = getPixels(image);
    var jobs = arrayListOf<Job>();
    val st = (image.height - 2*r)/lines;
    for (i in 0..lines - 1){
        val job = GlobalScope.launch {
            for (x in r..image.width - r)
                for (y in (r + st * i)..(r + st * (i + 1))){
                    var medium = findMedium(x, y, pixels, r);
                    ni.setRGB(x, y, medium);
                }
        }
        jobs.add(job);
    }
    jobs.forEach({j -> j.join()})
}


fun findMedium(x : Int, y : Int, pixels : Array<Array<Color>>, r : Int) : Int{
    var a = arrayOf(0,0,0);
    var medium : Int = 0;
    val rr = (r / 2)
    for (i in -rr..rr){
        for (j in -rr..rr){
            a[0] += pixels[y + j][x + i].getRed();
            a[1] += pixels[y + j][x + i].getGreen();
            a[2] += pixels[y + j][x + i].getBlue();
        }
    }
    a[0]/=(r*r);
    a[1]/=(r*r);
    a[2]/=(r*r);
    medium = (a[0] shl 16 and 0x00FF0000) or  (a[1] shl 8 and 0x0000FF00) or (a[2] and 0x000000FF);
    return medium;
}

fun getPixels(image : BufferedImage) : Array<Array<Color>>{
    val pixels = Array<Array<Color>>(image.height) {
            row ->
        Array<Color>(image.width) {
                column ->
            Color(image.getRGB(column, row))
        }
    }
    return pixels
}


