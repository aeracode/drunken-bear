package com.aerabox
package drunkenbear {
  
  trait Action
  case object Ignore extends Action
  case object Download extends Action
  case object UpdateLinkOnly extends Action

  case class FromTo(val from: String, val to: String)

  class ProcessResults(
    val urls: Seq[FromTo],
    val imgs: Seq[FromTo],
    val html: String)
}


