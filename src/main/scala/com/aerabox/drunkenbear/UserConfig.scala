package com.aerabox.drunkenbear

object UserConfig extends Configuration {

  val startUrl = "http://minecraft-ru.gamepedia.com/Industrial_Craft2"

  val outdir = "output/"

  val deep = 4

  val exclude = List(
    """.*index\.php\?.*""",
    """.*(Файл|Служебная|Участник):.*""",
    """.*Обсуждение.*""")

  val include = List("""http:\/\/minecraft-ru\.gamepedia\.com\/Industrial_Craft2.*""")

  val includeForFutureDownload = List("""http:\/\/minecraft-ru\.gamepedia\.com\/.*""")

}