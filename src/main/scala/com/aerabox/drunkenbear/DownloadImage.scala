package com.aerabox.drunkenbear

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

import scala.collection.mutable

object DownloadImage {

  val downloadedImages = mutable.HashSet.empty[String]

  def contains(file: String) = downloadedImages contains file

  def generateName(url: String) = "img/"+url.hashCode+".png"

  def apply(from: String, path: String, file: String): Option[Boolean] = {
    var out: OutputStream = null;
    var in: InputStream = null;

    try {
      val to = path + file
      val localfile = new File(to)
      if (localfile.exists) { return Some(false) }
      val url = new URL(from)
      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      in = connection.getInputStream
      out = new BufferedOutputStream(new FileOutputStream(localfile))
      val byteArray = Stream.continually(in.read).takeWhile(-1 != _).map(_.toByte).toArray
      out.write(byteArray)
      return Some(true)
    } catch {
      case e: Exception ⇒
        System.err.println(e.getMessage);
        return None
    } finally {
      if (out != null) out.close
      if (in != null) in.close
      downloadedImages += file
    }
  }

}