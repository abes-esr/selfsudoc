<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml"
            version="1.0"
            media-type="application/xml"
            encoding="UTF-8"
            indent="no" />
            
<xsl:param name="rcrselected" select="''"></xsl:param><!-- default is empty string -->

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/><!-- recursive copy -->
  </xsl:copy>
</xsl:template>

<!--  matching stop recursive copy process-->
<xsl:template match="datagroup[(@tag='loc')]">

<xsl:variable name="code" select="datafield[@tag='930']/subfield[@code='5']/text()" /><!-- matching RCR ? -->
<xsl:variable name="rcr" select="substring-before($code,':')" /><!-- matching RCR ? -->
<xsl:if test="$rcr!='' and contains($rcrselected,$rcr)">
<xsl:element name="datagroup"><!-- keep this datagroup -->
	<xsl:attribute name="tag">loc</xsl:attribute>
    <xsl:apply-templates select="@*|node()"/><!-- restart copy -->
</xsl:element>                    				
</xsl:if><!--  otherwise do nothing : ignore copy will remove tag -->      
</xsl:template>



</xsl:stylesheet>