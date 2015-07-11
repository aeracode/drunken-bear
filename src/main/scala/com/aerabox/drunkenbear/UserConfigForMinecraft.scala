package com.aerabox.drunkenbear

object UserConfigForMinecraft extends Configuration {

  val startUrl = "http://minecraft-ru.gamepedia.com/Шаблон:Модификации/Содержимое"

  val outdir = "output/"

  val deep = 5
  
  val checkWebForAlreadyDownloadedFile = false

  val exclude = List(
    """.*index\.php\?.*""",
    """.*(Файл|Служебная|Участни(к|ца)):.*""",
    """.*(Food_Plus_Mod|Galacticraft|Steve's_Carts_2).*""",
    """.*(Обсуждение|GregTech|Aether|Applied_Energistics|Traincraft|ExtraCells|RedPower2|Divine_RPG|Thermal_Expansion|Portal_Gun|Twilight_Forest|TerraFirmaCraft|ThaumCraft).*""")

  val include = List("""http:\/\/minecraft-ru\.gamepedia\.com\/.*""")
  val updateLinkOnly = List(
      """http:\/\/minecraft-ru\.gamepedia\.com\/Forestry.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/RailCraft.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/Industrial_Craft2.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/Better_Furnace_Mod.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/Biomes_O'_Plenty.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/BiblioCraft.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/CraftBook.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/Equivalent_Exchange_2.*""",
      """http:\/\/minecraft-ru\.gamepedia\.com\/BuildCraft.*""")
  val updatePriority = true
  
  

}