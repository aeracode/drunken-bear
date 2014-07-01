package com.aerabox.drunkenbear

import scala.xml.Node
import scala.xml.XML

object Template {

  def apply(src: String): String =

    /* * * * * * * * * * * * * * *
     *                           *
     *    HTML TEMPLATE          *
     *                           *
     * * * * * * * * * * * * * * */

    """<!-- 2014  -->
<html>
 <head>
  <meta charset="UTF-8">
  <link rel="stylesheet" type="text/css" href="style.css"/>
  <script src="script.js"></script>
 </head>
<body>"""+src+"""</body>
</html>"""

}