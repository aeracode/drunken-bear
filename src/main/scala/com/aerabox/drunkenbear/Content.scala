package com.aerabox.drunkenbear

import java.net.URI
import java.net.URL
import java.net.URLDecoder

import scala.collection.convert.wrapAsScala.asScalaIterator
import scala.collection.mutable

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

trait Content {

  final def process() = {
    clean()
    val hrefs = processHref();
    val srcs = processSrc();
    val html = getHtml();
    new ProcessResults(hrefs, srcs, html)
  }

  protected def clean(): Unit
  protected def getHtml(): String
  protected def processHref(): Seq[FromTo]
  protected def processSrc(): Seq[FromTo]
}

object Content {
  type FromToSeq = Seq[Pair[String, String]]
  def apply(jsoupDoc: Document, cfg: Configuration): Content = new JsoupConent(jsoupDoc, cfg)
}

/* * * * * * * * * * * * * * * * * * * * * *
 *                                         *
 *  ROUGH IMPLEMENTATION BASED ON JSOUP    *
 *                                         *
 * * * * * * * * * * * * * * * * * * * * * */

private class JsoupConent(doc: Document, cfg: Configuration) extends Content {

  private val UTF_8 = "utf-8"

  private val content = doc.select("div[id=content]")

  def clean(): Unit = content.select("script").remove()

  def processHref: Seq[FromTo] = {
    var i = 0;
    val urls = mutable.ArrayBuffer.empty[FromTo]
    for {
      a ← content.select("a").listIterator
      href ← getHref(a)
      url = URLDecoder.decode(ensureAbsoluteURL(doc.location, href), UTF_8)
    } cfg.actionFor(url) match {
      case Ignore         => // TODO: Do js confirm dialog
      case UpdateLinkOnly => updHref(a, cfg.localPath(url))
      case Download       => val file = cfg.localPath(url); updHref(a, file); urls += FromTo(url, file);
    }
    return urls
  }

  def processSrc: Seq[FromTo] = {
    val srcs = mutable.ArrayBuffer.empty[FromTo]
    for {
      img ← content.select("img").listIterator
      src = ensureAbsoluteURL(doc.location, img.attr("src").replaceAll("""\?version=(\d|[a-g])*""", ""))
    } {
      val path = DownloadImage.generateName(src)
      img.attr("src", path)
      srcs += FromTo(src, path)
    }
    return srcs
  }

  def getHtml(): String = content.outerHtml

  @inline def updHref(a: Element, href: String) = a.attr("href", href)
  @inline def getHref(a: Element): Option[String] = { val href = a.attr("href"); if (validHref(href)) Some(href) else None }

  private def validHref(href: String) = !(href.isEmpty || href.charAt(0) == '#')

  private def ensureAbsoluteURL(base: String, url: String) =
    if (url.startsWith("http")) url
    else if (url.startsWith("?")) base + url
    else
      try {
        URI.create(base).resolve(url).toString
      } catch {
        case e: IllegalArgumentException =>
          System.err.println(e.getMessage)
          new URL(new URL(base), url).toString
      }

}