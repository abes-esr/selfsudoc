<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:Script = "java:fr.abes.technic.RhinoScripting"
	exclude-result-prefixes="Script">
<xsl:output method="text" encoding="UTF-8" media-type="text/plain" indent="no" />

<xsl:param name="shortname" select="''"></xsl:param><!-- default is empty string -->
<xsl:param name="withcollections" select="''"></xsl:param><!-- default is empty string -->
<xsl:param name="runtimedir" select="''"></xsl:param><!-- default is empty string -->

<xsl:variable name="scriptInstance" select="Script:new('/home/batch/exportslibreservice/script.js','Cp1252')" ></xsl:variable>
		
<!--  Technic private templates BEGIN -->		

<xsl:template name="quotingdelimeter">

	<xsl:param name="souschaine" select="''" />

	<xsl:choose>
		<xsl:when test="contains($souschaine,'&#59;')">
			<xsl:value-of select="substring-before($souschaine,'&#59;')" /><!-- semicolon ? we must quote it 	-->
			<xsl:text><![CDATA[";"]]></xsl:text>
			<xsl:call-template name="quotingdelimeter"><!--  recursive -->
				<xsl:with-param name="souschaine"
					select="substring-after($souschaine,'&#59;')" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$souschaine" />
		</xsl:otherwise>
	</xsl:choose>

</xsl:template>	

		
<xsl:template name="doublingquote">

	<xsl:param name="souschaine" select="''" />

	<xsl:choose>
		<xsl:when test="contains($souschaine,'&#34;')">
			<xsl:value-of select="substring-before($souschaine,'&#34;')" /><!-- quote ? we must double it 	-->
			<xsl:text><![CDATA[""]]></xsl:text>
			<xsl:call-template name="doublingquote"><!--  recursive -->
				<xsl:with-param name="souschaine"
					select="substring-after($souschaine,'&#34;')" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>			
				<xsl:value-of select="$souschaine" />			
		</xsl:otherwise>
	</xsl:choose>

</xsl:template>
	
	
		
		
	<xsl:template name="donnee">
		<xsl:param name="cx" />
		<xsl:param name="cy" />
		<xsl:param name="valeur" />
			<xsl:if test="number($cx)=1"><!-- trick for PPN column format -->
				<xsl:text>=</xsl:text>
			</xsl:if>
			<xsl:text>"</xsl:text>
		<xsl:choose>
			<xsl:when test="contains($valeur,'&#34;')"><!-- quote ? we must double it -->
				<xsl:call-template name="doublingquote">
				<xsl:with-param name="souschaine" select="translate($valeur,'ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïñòóôõöùúûüýÿ°', 'AAAAAACEEEEIIIINOOOOOUUUUYaaaaaaceeeeiiiinooooouuuuyyo')" />
			</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>				
					<xsl:value-of select="translate($valeur,'ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïñòóôõöùúûüýÿ°', 'AAAAAACEEEEIIIINOOOOOUUUUYaaaaaaceeeeiiiinooooouuuuyyo')" />				
			</xsl:otherwise>
		</xsl:choose>
			<xsl:text>"</xsl:text>
		<xsl:if test="number($cx)&lt;36">
			<xsl:text><![CDATA[;]]></xsl:text><!-- semicolon -->
		</xsl:if>		
	</xsl:template>


	<!--  Technic private templates END -->


	<xsl:template match="/catalogue">
		<xsl:choose>
			<xsl:when test="function-available('Script:evaluate')">
				<!-- Java extension found -->
				<xsl:value-of select="Script:evaluate($scriptInstance,1)" />
				<!-- script evaluated from line number 1-->
			</xsl:when>
			<xsl:otherwise>
				function is not available
			</xsl:otherwise>
		</xsl:choose>		
		<xsl:call-template name="SYLK_COLUMN_NAMES"></xsl:call-template>
		<xsl:apply-templates select="record"></xsl:apply-templates>		
	</xsl:template>

	<xsl:template name="SYLK_COLUMN_NAMES">
		<!-- Titre des colonnes -->
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'1'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'PPN'" />
		</xsl:call-template>		
		<xsl:text>&#10;</xsl:text><!-- LineFeed -->
	</xsl:template>

	<xsl:template match="record">
		<xsl:variable name="ligne" select="position()+1"></xsl:variable>
		<xsl:call-template name="PPN">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>		
	<xsl:text>&#10;</xsl:text><!-- LineFeed -->
	</xsl:template>


	<xsl:template name="PPN">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:value-of select="controlfield[@tag='001']" />
		</xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'1'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>