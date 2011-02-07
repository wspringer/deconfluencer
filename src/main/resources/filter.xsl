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
          <div id="top"></div>
          <div id="masthead">
            <xsl:attribute name="style">
              <xsl:text>background-color: </xsl:text>
              <xsl:apply-templates select="$card" mode="color"/>
            </xsl:attribute>
            <xsl:apply-templates select="$card/front" mode="card"/>
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
  
  <xsl:template match="div[@id='card-images']"/>

  <xsl:template match="script"/>

  <xsl:template match="front" mode="card">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="back" mode="card"/>

  <xsl:template match="para" mode="card">
    <xsl:apply-templates/>
    <br/>
  </xsl:template>

  <xsl:template match="card" mode="color">
    <xsl:variable name="category" select="./@category"/>
    <xsl:value-of select="document('/Users/wilfred/workspace/craftmanship/src/cards/final.xml')//category[@id=$category]/@color"/>
  </xsl:template>

</xsl:stylesheet>
