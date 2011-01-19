<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:template match="/">
        <html>
            <body>
                <xsl:copy-of select="//DIV[@class='wiki-content']"/>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
