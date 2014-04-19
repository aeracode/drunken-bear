package com.aerabox.drunkenbear

import java.io.OutputStream
import java.io.InputStream
import java.net.URL
import java.net.HttpURLConnection
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.File

object DownloadImage {

  def generateName(url: String) = url.hashCode+".png"

  def apply(from: String, to: String) {
    var out: OutputStream = null;
    var in: InputStream = null;

    try {
      val url = new URL(from)
      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      in = connection.getInputStream
      val localfile = new File(to)
      if (localfile.exists) { print("o"); return }
      print("*");
      out = new BufferedOutputStream(new FileOutputStream(localfile))
      val byteArray = Stream.continually(in.read).takeWhile(-1 != _).map(_.toByte).toArray
      out.write(byteArray)
    } catch {
      case e: Exception ⇒ System.err.println(e.getMessage)
    } finally {
      if (out != null) out.close
      if (in != null) in.close
    }
  }

}