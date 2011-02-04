<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:str="http://exslt.org/strings"
                xmlns:digest="xalan://org.apache.commons.codec.digest.DigestUtils"
                extension-element-prefixes="digest"
                exclude-result-prefixes="str digest"
                version="1.0">

  <xsl:output method="html"/>
  <xsl:param name="stylesheet"/>
  <xsl:param name="path"/>
  
  <xsl:template match="/">
    <xsl:variable name="id" select="substring($path, 2)"/>
    <xsl:variable name="card" 
                  select="document('/Users/wilfred/workspace/craftmanship/src/cards/final.xml')/deck/card[@id=$id]"/>
    <xsl:variable name="title">
      <xsl:apply-templates 
          select="$card/front/para/text()"/>
    </xsl:variable>
    <html>
      <head>
        <link rel="stylesheet"
              type="text/css">
          <xsl:attribute name="href">
            <xsl:value-of select="$stylesheet"/>
          </xsl:attribute>
        </link>
      </head>
      <body>
        <div id="main">
          <div id="masthead">xebia <span id="essentials">essentials</span></div>
          <div id="body">
            <h1><xsl:value-of select="$title"/></h1>
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
  
  <xsl:template match="div[@id='card-images']"/>

  <xsl:template match="script"/>

</xsl:stylesheet>
