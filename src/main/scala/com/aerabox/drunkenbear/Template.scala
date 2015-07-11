package com.aerabox.drunkenbear

object Template {

  def apply(src: String): String =

    /* * * * * * * * * * * * * * *
     *                           *
     *    HTML TEMPLATE          *
     *                           *
     * * * * * * * * * * * * * * */

    """<!-- 2015  -->
<html>
 <head>
  <meta charset="UTF-8">
  <link rel="stylesheet" type="text/css" href="style.css"/>
  <script src="script.js"></script>
 </head>
<body>"""+src+"""</body>
</html>"""

}