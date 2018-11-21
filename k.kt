import kotlinx.coroutines.*
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

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

fun blurRows(r : Int, lines : Int, image : BufferedImage, ni : BufferedImage) = runBlocking {
    val pixels = getPixels(image);
    var jobs = arrayListOf<Job>();
    val st = (image.width - 2*r)/lines;
    for (i in 0..lines - 1){
        val job = GlobalScope.launch {
            for (x in (r + st * i)..(r + st * (i + 1)))
                for (y in r..image.height - (r + 1)){
                    var medium = findMedium(x, y, pixels, r);
                    ni.setRGB(x, y, medium);
                }
        }
        jobs.add(job);
    }
    jobs.forEach({j -> j.join()})
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


fun benchMark(r : Int, threads : Int, image : BufferedImage, ni : BufferedImage) = runBlocking {
    var avrg : Long = 0;
    for (i in 1..10){
        var startTime = System.currentTimeMillis();
        blurRows(r, threads, image, ni);
        avrg += System.currentTimeMillis() - startTime;
    }
    avrg /=10;
    println("row: ${avrg}")

    avrg = 0;
    for (i in 1..10){
        var startTime = System.currentTimeMillis();
        blurColumns(r, threads, image, ni);
        avrg += System.currentTimeMillis() - startTime;
    }
    avrg /=10;
    println("columns: ${avrg}")
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

// Analisis Data
/*
575 x 938
1 thread: 597
1 thread for every pixel: 713
threads: 2
row: 255
columns: 249
threads: 4
row: 161
columns: 132
threads: 6
row: 124
columns: 117
threads: 8
row: 109
columns: 106
5947 x 3970
1 thread: 33232
1 thread for every pixel: 35788
threads: 2
row: 16200
columns: 14828
threads: 4
row: 10908
columns: 10062
threads: 6
row: 9447
columns: 8630
threads: 8
row: 8870
columns: 7513
*/
