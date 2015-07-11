package com.aerabox.drunkenbear

import java.net.URI
import java.net.URL

trait Configuration {

  val startUrl: String
  val outdir: String
  val deep: Int
  val checkWebForAlreadyDownloadedFile: Boolean
  val exclude: List[String]
  val include: List[String]
  val updateLinkOnly: List[String]
  val updatePriority: Boolean

  val winBadChars = """[\\/:*?"<>|]"""

  def localPath(webUrl: String): String = {
    val name = try {
      URI.create(webUrl).getPath.substring(1)
    } catch {
      case e: IllegalArgumentException => new URL(webUrl).getPath
    }
    name.replaceAll(winBadChars, "-")+".html"
  }

  def actionFor(url: String): Action = {
    if (exclude exists url.matches) Ignore
    else if (updatePriority) {
      if (updateLinkOnly exists url.matches) UpdateLinkOnly
      else if (include exists url.matches) Download
      else Ignore
    } else {
      if (include exists url.matches) Download
      else if (updateLinkOnly exists url.matches) UpdateLinkOnly
      else Ignore
    }

  }

}

