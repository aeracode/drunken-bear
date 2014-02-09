package com.aerabox.drunkenbear

import java.io.File
import java.io.FileWriter

import collection.convert.wrapAsScala.asScalaIterator

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object Config {
  val userAgent = "Mozilla"
  val timeoutMillis = 3000
  val defaultContent = ""
}

object Main extends App {

  import Debug._
  import Config._

  def process(url: String): Unit = {

    def loadUrl(url: String): Document = {
      log(s"\tloadUrl: $url")

      Jsoup.connect(url)
        .userAgent(userAgent)
        .followRedirects(true)
        .get()

      // using test data
      //Jsoup.parse(new File("input.html"), "UTF-8")
    }

    def getContent(doc: Document) = {
      log(s"\tgetting content "+"div[id=content]")
      doc.select("div[id=content]")
    }

    def save(s: String, file: String) {
      log(s"\tsave to $file")
      Some(new FileWriter(file)) foreach { writer => writer.write(s); writer.close }
    }

    log(s"process: $url")

    val raw = loadUrl(url)
    val content = getContent(raw)

    // content remove dirt
    content.select("script").remove()

    // apply template
    // save for file
    save(content.outerHtml, "output.html")
    // content get urls
    content.select("a").listIterator foreach { a => println(a.attr("href")) }
    // mark porcessed
    // urls filter (processed) do process 
  }

  process("https://www.google.com/")

}

object Debug {
  def log(s: String) = println(s)
}