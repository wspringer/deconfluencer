<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:str="http://exslt.org/strings"
                xmlns:digest="xalan://org.apache.commons.codec.digest.DigestUtils"
                extension-element-prefixes="digest"
                exclude-result-prefixes="str digest"
                version="1.0">

  <xsl:output method="html"/>
  <xsl:param name="path"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet"
              type="text/css"
              href="/resources/style.css"/>
      </head>
      <body>
        <div id="main">
          <div id="top"></div>
          <div id="masthead">
            <table cellspacing="0" cellpadding="0" border="0">
              <tr>
                <td valign="top"><img src="/resources/deconfluencer-small.png"/></td>
                <td valign="top" style="padding-left: 10px">
                  <h1>
                    <xsl:value-of select="//span[@id='title-text']/a/text()"/>
                  </h1>
                </td>
              </tr>
            </table>
          </div>
          <div id="body">
            <xsl:apply-templates select="//div[@class='wiki-content']"/>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="a[@href]">
    <xsl:copy>
      <xsl:attribute name="href">
        <xsl:value-of select="str:split(@href, '/')[last()]"/>
      </xsl:attribute>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="div[@class='error']"/>
  
  <xsl:template match="script"/>

</xsl:stylesheet>
