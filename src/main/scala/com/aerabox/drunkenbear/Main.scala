package com.aerabox.drunkenbear

import java.io.FileWriter
import java.net.URL
import java.util.regex.PatternSyntaxException
import scala.collection.convert.wrapAsScala.asScalaIterator
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import com.aerabox.drunkenbear.Template.Tpl
import com.aerabox.drunkenbear.Debug.log
import org.jsoup.nodes.Element
import java.net.URI

case class Config(startUrl: String = "http://minecraft-ru.gamepedia.com/Заглавная_страница",
                  userAgent: String = "Mozilla",
                  timeoutMillis: Int = 3000,
                  defaultContent: String = "",
                  outputDir: String = ".",
                  dirtSelector: String = "script",
                  contentMap: Map[String, String] = Map("content" -> "div[id=content]", "head" -> "head"),
                  template: List[Tpl] = Template.parse("<html>${head}<body>${content}</body></html>")) {

  val host = new URI(startUrl).getHost
  println("HOST"+host)
}

object Main extends App {

  val processed: collection.mutable.Set[String] = collection.mutable.Set.empty

  def process(startUrl: String, config: Config = new Config): Unit = {

    def loadUrl(url: String): Document = {
      log("\tloadUrl: "+url)

      Jsoup.connect(url)
        .userAgent(config.userAgent)
        .followRedirects(true)
        .get()

      // using test data
      //Jsoup.parse(new File("input.html"), "UTF-8")
    }

    def getContent(doc: Document, selectors: Map[String, String]) = {
      log("\tgetContent")
      val result = selectors map { p => log(s"\t\tgetting content $p"); p._1 -> doc.select(p._2) }
      log("\tend of getContent")
      result
    }

    def save(s: String, file: String) {
      log(s"\tsave to $file")
      Some(new FileWriter(file)) foreach { writer => writer.write(s); writer.close }
    }

    def template(tpl: List[Template.Tpl], content: Map[String, Elements]) = {
      tpl map {
        case Template.Str(s)    => s
        case Template.Sub(name) => content.get(name) match { case Some(e) => e.outerHtml; case None => ""; }
      } mkString "\n"
    }

    def valid(href: String): Boolean = {

      def localUrl(url: String): Boolean = {
        val uri = new URI(url)
        val host = uri.getHost
        val path = uri.getPath
        if (path == null || path.isEmpty) false
        else if (host == null) true
        else if (host.isEmpty) true
        else if (host == config.host) true
        else false
      }

      val result = if (href == null) false
      else if (href.isEmpty) false
      else localUrl(href)

      log(s"VALID: $href? $result")

      result
    }

    def correctHost(href: String, host: String): String = {
      log(s"CORRECTING: $href")
      val uri = new URI(href)
      val host = uri.getHost
      if (host == null || host.isEmpty) "http://"+config.host + href
      else href
    }

    log(s"process: $startUrl")

    // mark porcessed
    processed += startUrl;

    val url = correctHost(startUrl, config.host)
    println("URL "+url)

    try {

      val raw = loadUrl(url)
      val content = getContent(raw, config.contentMap)

      // content remove dirt
      content foreach { _._2.select(config.dirtSelector).remove() }

      val out = template(config.template, content)

      // save for file
      save(out, config.outputDir + new URI(url).getPath+".html")

      // content get urls
      for {
        (_, es) <- content
        a <- es.select("a").listIterator
        href = a.attr("href")
        if !processed.contains(href) && valid(href)
      } process(href, config)

    } catch {
      case e: Throwable => log("FAIL: "+url); e.printStackTrace
    }
  }

  val config = new Config
  process(config.startUrl, config)

}

object Template {
  sealed trait Tpl
  case class Str(s: String) extends Tpl
  case class Sub(name: String) extends Tpl

  def parse(s: String) = parseImpl(s, Nil)

  private def parseImpl(s: String, result: List[Tpl]): List[Tpl] = {
    def parseSub(s: String): String = s.split('}')(0)
    def parseStr(s: String): String = s.split('$')(0)

    if (s.isEmpty) result
    else s.head match {
      case '$' =>
        val name = parseSub(s.substring(2));
        log("\t\tfount template val "+name)
        parseImpl(s.substring(name.length + 3), result :+ Sub(name))
      case _ =>
        val str = parseStr(s);
        log("\t\tfount string val "+str)
        parseImpl(s.substring(str.length), result :+ Str(str))
    }
  }
}

object Debug { def log(s: String) = println(s) }