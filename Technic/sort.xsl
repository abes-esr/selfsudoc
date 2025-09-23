<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml"
            version="1.0"
            media-type="application/xml"
            encoding="UTF-8"
            indent="no" />

<xsl:template match="catalogue">
<xsl:element name="catalogue">
<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>  
    <xsl:apply-templates select="record">
    	<xsl:sort case-order="lower-first" select="datafield[@tag='200']/subfield[(@code='a' and count(following-sibling::subfield[@code='classified-a'])=0) or (@code='classified-a')]"></xsl:sort>
    </xsl:apply-templates>

</xsl:element>      
</xsl:template>

<xsl:template match="record">
	<xsl:copy-of select="."></xsl:copy-of>
</xsl:template>



</xsl:stylesheet>