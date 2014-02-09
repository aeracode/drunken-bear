package com.aerabox.drunkenbear

import language.postfixOps
import language.implicitConversions
import org.jsoup.Jsoup
import java.io.File
import org.jsoup.safety.Whitelist
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import java.io.FileWriter
import sys.process.stringToProcess
import java.io.PrintWriter

object TryingJsuop extends App {

  val startUrl = "http://minecraft-ru.gamepedia.com/Заглавная_страница"
  val outputDir = "G:/siterips"

  val input = new File(outputDir+"/input.html")
  val doc = Jsoup.parse(input, "UTF-8", "http://minecraft-ru.gamepedia.com/")
  val cleaned = doc.getElementById("content") 
  val newDoc = new Document("");
  val html = newDoc.appendElement("html")
  html.appendChild(doc.head)
  html.appendElement("body").appendChild(cleaned)
  
  Some(new PrintWriter(outputDir+"/jsoup.html")).foreach{ p => p.write(html.outerHtml); p.close }

}