package com.aerabox.drunkenbear

object UserConfig extends Configuration {

  val startUrl = "http://terraria.gamepedia.com/Acorn"

  val outdir = "output/"

  val deep = 5
  
  val checkWebForAlreadyDownloadedFile = false

  val exclude = List(
    """.*index\.php\?.*""",
    """.*(File|Special|User):.*""",
    """http:\/\/terraria\.gamepedia.com\/.*?/.."""
    )

  val include = List("""http:\/\/terraria\.gamepedia.com\/.*""")
  val updateLinkOnly = List()
  val updatePriority = true
  
  

}