package com.aerabox.drunkenbear

import java.io.FileWriter
import java.net.URI
import java.net.URL
import java.net.URLDecoder

import scala.collection.mutable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

object Downloader extends App {

  val cfg = UserConfig

  def getWeb(url: String) = Jsoup.connect(url).userAgent("Mozilla").followRedirects(true).timeout(30000).get()
  def save(content: String, file: String): Unit = {
    if (new File(file).exists) { print("F"); return }
    Some(new FileWriter(file)) foreach { writer ⇒ writer.write(content); writer.close }
  }

  val downloaded = mutable.HashSet.empty[String]

  var totalUrls = 0
  var processedUrls = 0
  var passedUrls = 0

  def process(html: Document) = {

    val results = Content(html, cfg).process()

    print("U:"+results.urls.size)

    print("\tI[")
    for (FromTo(web, file) ← results.imgs)
      if (DownloadImage contains file) print(".")
      else DownloadImage(web, cfg.outdir, file) match {
        case Some(true)  ⇒ print("*")
        case Some(false) ⇒ print("o")
        case None        ⇒ print("!")
      }
    println("]")

    (results.urls, Template(results.html))

  }

  private def download(from: String, to: String, deep: Int) {
    if (deep > cfg.deep) return

    println(s"\n[$deep] ${downloaded.size}+$passedUrls=${downloaded.size + passedUrls}|$totalUrls\t$to\n$from")

    try {
      val (urls, content) = process(getWeb(from))
      save(content, cfg.outdir + to)
      downloaded += to
      totalUrls += urls.size;
      for (FromTo(web, file) ← urls) if (downloaded contains file) passedUrls += 1 else {
        if (!cfg.checkWebForAlreadyDownloadedFile && new File(cfg.outdir + file).exists) {
          print("+");
        } else {
          download(web, file, deep + 1)
        }
      }
    } catch { case e: Exception ⇒ System.err.println(e.getClass.getName+": "+e.getMessage); downloaded += to }
  }

  def download(): Unit = download(cfg.startUrl, cfg.localPath(cfg.startUrl), 0)

  download()

}