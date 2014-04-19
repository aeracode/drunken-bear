package com.aerabox.drunkenbear

import java.io.FileWriter
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import scala.collection.convert.wrapAsScala.asScalaIterator
import scala.collection.mutable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

object Downloader extends App {

  val outdir = "output/"

  val exclude = List(
    """.*index\.php\?.*""",
    """.*(Файл|Служебная|Участник):.*""",
    """.*Обсуждение.*""")

  val include = List(
    """http:\/\/minecraft-ru.gamepedia.com\/.*""")

  val chars = """[\\/:*?"<>|]"""

  val maxDeep = 5

  val utf8 = "utf-8"

  val imgOutDir = outdir+"img/"

  def getWeb(url: String) = Jsoup.connect(url).userAgent("Mozilla").followRedirects(true).timeout(30000).get()
  def save(content: String, file: String) = Some(new FileWriter(file)) foreach { writer ⇒ writer.write(content); writer.close }

  def isAllowed(url: String) = (include forall url.matches) && !(exclude exists url.matches)
  def validHref(href: String) = !(href.isEmpty || href.charAt(0) == '#')

  def localPath(webUrl: String): String = {
    (try {
      val uri = URI.create(webUrl)
      uri.getPath.substring(1)
    } catch {
      case e: IllegalArgumentException ⇒
        System.err.println(e.getMessage)
        val url = new URL(webUrl)
        url.getPath
    }).replaceAll(chars, "-")+".html"
  }

  def ensureAbsoluteURL(base: String, url: String) =
    if (url.startsWith("http")) url
    else if (url.startsWith("?")) base + url
    else
      try {
        URI.create(base).resolve(url).toString
      } catch {
        case e: IllegalArgumentException ⇒
          System.err.println(e.getMessage)
          new URL(new URL(base), url).toString
      }

  val downloaded = mutable.HashSet.empty[String]
  val downloadedImages = mutable.HashSet.empty[String]
  var totalUrls = 0
  var processedUrls = 0

  def process(html: Document) = {

    // fetch content to save
    val content = html.select("div[id=content]")

    // clean
    content.select("script").remove()

    val urls = mutable.ArrayBuffer.empty[(String, String)]

    for {
      a ← content.select("a").listIterator
      href = a.attr("href")
      if validHref(href)
      url = URLDecoder.decode(ensureAbsoluteURL(html.location, href), utf8)
      if isAllowed(url)
    } {
      val path = localPath(url)
      a.attr("href", path)
      urls += ((url, path))
    }

    val urlsCount = urls.length
    totalUrls += urlsCount;

    print(" U:"+urlsCount)
    print("\tI[")

    for {
      (from, to) ← for {
        img ← content.select("img").listIterator
        src = ensureAbsoluteURL(html.location, img.attr("src").replaceAll("""\?version=(\d|[a-g])*""", ""))
      } yield {
        val path = DownloadImage.generateName(src)
        img.attr("src", path)
        (src, path)
      }
      if { val cached = downloadedImages contains to; print{ if (cached) "." else "" }; !cached }
    } {
      downloadedImages += to
      DownloadImage(from, imgOutDir + to)
    }

    println("]")

    (urls, """<html><head><link rel="stylesheet" type="text/css" href="style.css"><script src="script.js"></script></head><body>"""+content.outerHtml+"""</body></html>""")

  }

  /** Download page and subpages
    *
    * @param from
    * @param to where file should be stored.
    * @param deep
    */
  def download(from: String, to: String, deep: Int) {

    if (deep > maxDeep) return

    println("["+deep+"] "+downloaded.size+"|"+totalUrls+"\t"+to+"\n"+from+"")

    try {
      val (urls, content) = process(getWeb(from))
      save(content, outdir + to)
      downloaded += to
      for ((from, to) ← urls if !(downloaded contains to)) {
        download(from, to, deep + 1)
      }
    } catch {
      case e ⇒ System.err.println(e.getMessage)
    }
  }

  val index = "http://minecraft-ru.gamepedia.com/Заглавная_страница"
  download(index, localPath(index), 0)

}