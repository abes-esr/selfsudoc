<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:marc="http://www.loc.gov/MARC21/slim">	
<xsl:output method="xml"
			version="1.0"
			media-type="application/xml"
			encoding="UTF-8"
			indent="no" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
			<!-- recursive copy	-->
		</xsl:copy>
	</xsl:template>


	<xsl:template match="subfield[contains(text(),'&#152;')]|marc:subfield[contains(text(),'&#152;')]"><!-- \u0098 -->
		<!--  matching stop recursive copy process-->
		<xsl:variable name="before98" select="substring-before(text(),'&#152;')" /><!--	\u0098	-->
		<xsl:variable name="after98" select="substring-after(text(),'&#152;')" /><!-- \u0098 -->
		<xsl:variable name="after98before9C" select="substring-before($after98,'&#156;')" /><!-- \u009C	-->
		<xsl:variable name="after9C" select="substring-after($after98,'&#156;')" /><!--	\u009C -->		
		<xsl:choose>
			<xsl:when test="contains(./text(),'&#156;')"><!-- \u009C  = well formed-->
				<xsl:element name="subfield" namespace="{namespace-uri()}"><!-- cleaned -->				
					<xsl:attribute name="code"><xsl:value-of select="./@code" /></xsl:attribute>
					<xsl:value-of select="concat($before98,$after98before9C,$after9C)"></xsl:value-of>
				</xsl:element>
				<xsl:element name="subfield" namespace="{namespace-uri()}"><!-- for classification -->
					<xsl:attribute name="code">classified-<xsl:value-of select="./@code" /></xsl:attribute>
					<xsl:choose>
						<xsl:when test="not($after9C='')"><!-- starting with non-classified -->
							<xsl:value-of select="$after9C"></xsl:value-of>
						</xsl:when>
						<xsl:otherwise><!-- ending with non-classified : we swap !-->
							<xsl:value-of select="$before98"></xsl:value-of>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise><!-- only 2 parts -->
				<xsl:element name="subfield" namespace="{namespace-uri()}"><!-- cleaned -->
					<xsl:attribute name="code"><xsl:value-of select="./@code" /></xsl:attribute>
					<xsl:value-of select="concat($before98,$after98)"></xsl:value-of>
				</xsl:element>				
				<xsl:element name="subfield" namespace="{namespace-uri()}"><!-- for classification -->
					<xsl:attribute name="code">classified-<xsl:value-of	select="./@code" /></xsl:attribute>
					<xsl:value-of select="$after98"></xsl:value-of>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



</xsl:stylesheet>