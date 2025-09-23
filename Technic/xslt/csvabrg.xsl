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
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'2'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Titre'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'3'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Issn'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'4'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Localisations'" />
		</xsl:call-template>
		<xsl:text>&#10;</xsl:text><!-- LineFeed -->
	</xsl:template>

	<xsl:template match="record">
		<xsl:variable name="ligne" select="position()+1"></xsl:variable>
		<xsl:call-template name="PPN">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Titre">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>		
		<xsl:call-template name="Issn">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Localisations">
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

	<xsl:template name="Titre">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:value-of select="datafield[@tag='200']/subfield[(@code='a' and count(following-sibling::subfield[@code='classified-a'])=0) or (@code='classified-a')]" /><xsl:if test="datafield[@tag='200']/subfield[@code='h'] != ''">.&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='h']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='i'] != ''">.&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='i']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='e'] != ''">&#32;:&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='e']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='b'] != ''">&#32;[<xsl:value-of select="datafield[@tag='200']/subfield[@code='b']" />]</xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='f'] != ''">&#32;/&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='f']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='g'] != ''">&#32;;&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='g']" /></xsl:if></xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'2'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>


	<xsl:template name="Issn">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:value-of select="datafield[@tag='011']/subfield[@code='a']" />
		</xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'3'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>
	
<xsl:template name="Localisations">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:for-each select="datagroup[@tag='loc']"><xsl:if test= "(position() > 1 )">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_955" /><xsl:call-template name="Tag_916" /><xsl:call-template name="Tag_930" />
		</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'4'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
	
	<xsl:template name="Tag_955"><xsl:for-each select="datafield[@tag='955']"><xsl:value-of select="subfield[@code='r']" /></xsl:for-each>
	</xsl:template>
	
<xsl:template name="Tag_930"><xsl:for-each select="datafield[@tag='930']"><xsl:if test="(subfield[@code='a'] != '')">&#32;[<xsl:value-of select="subfield[@code='a']" />]</xsl:if><xsl:if test="subfield[@code='c'] != ''"><xsl:text> </xsl:text><xsl:value-of select="subfield[@code='c']" /></xsl:if><xsl:if test="subfield[@code='d'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='d']" /></xsl:if><xsl:if test="subfield[@code='l'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='l']" /></xsl:if><xsl:if test="subfield[@code='e'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='e']" /></xsl:if></xsl:for-each></xsl:template>
	
	<xsl:template name="Tag_916"><xsl:for-each select="datafield[@tag='916']">
			<xsl:if test="(subfield[@code='a'] != '')"><xsl:variable name="arg0" select="string(subfield[@code='a'])"/>Durée de conservation : <xsl:value-of select="Script:call($scriptInstance,'Traite916',$arg0)" /></xsl:if>
		</xsl:for-each>
	</xsl:template>


	


</xsl:stylesheet>