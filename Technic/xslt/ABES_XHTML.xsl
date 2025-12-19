<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html[
<!ENTITY nbsp   "&#160;"> <!-- no-break space = non-breaking space, U+00A0 ISOnum -->
<!ENTITY agrave "&#224;"> <!-- latin small letter a with graveb = latin small letter a grave, U+00E0 ISOlat1 -->
]>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:Script = "java:fr.abes.technic.RhinoScripting"
                exclude-result-prefixes="Script">
<xsl:output method="xml"
            version="1.0"
            encoding="UTF-8"
            doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
			doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

<xsl:param name="excludeddatafields" select="''"></xsl:param><!-- default is empty string -->
<xsl:param name="shortname" select="''"></xsl:param><!-- default is empty string -->
<xsl:param name="withcollections" select="''"></xsl:param><!-- default is empty string -->
<xsl:param name="runtimedir" select="''"></xsl:param><!-- default is empty string -->

	<xsl:variable name="scriptInstance" select="Script:new('/data/script.js','Cp1252')"></xsl:variable>

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
	<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">	
			<head><title></title><!--  <style type="text/css">	
				.borderodd { border-style:solid; border-width: 1px; border-color:white #dddddd black; }
			</style> --></head>
			<body>
				<div>
					<img align="left" src="/data/Bloclogoadresse1.gif" width="236" height="77"/>
				</div>
				<br/>
			<div style="text-align:center" leading="100f,0f" >&nbsp;<br/>&nbsp;</div>		
		<div style="text-align:center" leading="0f,1.5f" >
			<font size="5" face="arial" style="bold"><b><xsl:choose>
				<xsl:when test="$withcollections">CATALOGUE DES RESSOURCES CONTINUES</xsl:when>
				<xsl:otherwise>CATALOGUE DES PERIODIQUES</xsl:otherwise>
			</xsl:choose><br></br>
			RCR N°<xsl:value-of select="//datagroup[@tag='loc']/datafield[@tag='930']/subfield[@code='b']" /><br></br>
			<xsl:value-of select="$shortname"></xsl:value-of></b></font>
		</div>		
		<div style="text-align:center" leading="120f,0f" >&nbsp;<br/>&nbsp;<br/></div>				
		<p style="page-break-after : always" />
		<p style="page-break-after : always" />		
		<xsl:apply-templates select="record"></xsl:apply-templates>		
			</body>
			</html>		
</xsl:template>
	
	
<xsl:template match="record">
<p><!-- needed to set leading for paragraph -->
<hr width="100%" size="1"></hr>
				<xsl:call-template name="PPN"></xsl:call-template>				
				<xsl:if test="not(contains($excludeddatafields,'200'))"><xsl:call-template name="Titre"></xsl:call-template></xsl:if>				
				<xsl:if test="not(contains($excludeddatafields,'210'))"><xsl:call-template name="Editions"></xsl:call-template></xsl:if>				
					<xsl:if test="not(contains($excludeddatafields,'011'))"><xsl:call-template name="Issn"></xsl:call-template></xsl:if>
					<xsl:if test="datafield[@tag='856']/subfield[@code='u'] != ''"><xsl:call-template name="Url"></xsl:call-template></xsl:if><!-- Level 0 -->
					<xsl:if test="not(datafield[@tag='856']/subfield[@code='u'] != '') and (datagroup[@tag='loc']/datafield[@tag='856']/subfield[@code='u'] != '')"><br/><b>Accès en ligne&nbsp;:</b></xsl:if>
					<xsl:for-each select="datagroup[@tag='loc']"><xsl:call-template name="UrlExemplaire" /></xsl:for-each>									
					<xsl:if test="not(contains($excludeddatafields,'4XX'))"><xsl:call-template name="Supplément"></xsl:call-template>			
					<xsl:call-template name="PubMereSupplément"></xsl:call-template>				
					<xsl:call-template name="PubAvec"></xsl:call-template>				
					<xsl:call-template name="SuiteDe"></xsl:call-template>				
					<xsl:call-template name="Succede"></xsl:call-template>				
					<xsl:call-template name="Remplace"></xsl:call-template>				
					<xsl:call-template name="RemplacePart"></xsl:call-template>				
					<xsl:call-template name="Absorbe"></xsl:call-template>				
					<xsl:call-template name="AbsorbePart"></xsl:call-template>				
					<xsl:call-template name="FusionDe"></xsl:call-template>				
					<xsl:call-template name="SuitePart"></xsl:call-template>				
					<xsl:call-template name="Devient"></xsl:call-template>				
					<xsl:call-template name="DevientPar"></xsl:call-template>				
					<xsl:call-template name="RemplacePar"></xsl:call-template>				
					<xsl:call-template name="RemplacePartPar"></xsl:call-template>				
					<xsl:call-template name="AbsorbePar"></xsl:call-template>				
					<xsl:call-template name="AbsorbePartPar"></xsl:call-template>				
					<xsl:call-template name="ScindeEn"></xsl:call-template>				
					<xsl:call-template name="Redevient"></xsl:call-template>				
					<xsl:call-template name="AutreEdition"></xsl:call-template>				
					<xsl:call-template name="AutreEditionAutreSupp"></xsl:call-template>				
					<xsl:call-template name="TraduitSousTitre"></xsl:call-template>				
					<xsl:call-template name="TraduitDe"></xsl:call-template>				
					<xsl:call-template name="ReproductionDe"></xsl:call-template>				
					<xsl:call-template name="ReproduitComme"></xsl:call-template>
					</xsl:if>									
					<xsl:if test="not(contains($excludeddatafields,'5XX'))"><xsl:call-template name="TitreCle"></xsl:call-template>				
					<xsl:call-template name="TitreAbrege"></xsl:call-template>				
					<xsl:call-template name="TitreParallele"></xsl:call-template>				
					<xsl:call-template name="TitreDeCouv"></xsl:call-template>
					</xsl:if>												
					<xsl:call-template name="PublieEn"></xsl:call-template>
					<xsl:call-template name="Dewey"></xsl:call-template>					
					<xsl:if test="not(contains($excludeddatafields,'8XX'))"><xsl:call-template name="SourceCat"></xsl:call-template></xsl:if>
<!--
					<xsl:if test="datagroup[@tag='loc']/datafield[@tag='959']/subfield[@code='r'] != ''">
					<br/><b>Lacunes&nbsp;:</b><xsl:for-each select="datagroup[@tag='loc']"><xsl:call-template name="EtatLacune"></xsl:call-template></xsl:for-each>
					</xsl:if>
-->
					<xsl:if test="datagroup[@tag='loc']/datafield[@tag='930']/subfield[@code='z'] != ''"><!--  at least one PCP code -->
					<br/><b>Plan de Conservation Partagée&nbsp;:</b><xsl:for-each select="datagroup[@tag='loc']"><xsl:call-template name="PCP" /></xsl:for-each>
					</xsl:if>										
					<xsl:if test="not(contains($excludeddatafields,'9XX'))"><xsl:call-template name="Localisations"></xsl:call-template></xsl:if>
</p>										
</xsl:template>		

	<xsl:template name="PPN"><em><b><xsl:value-of select="controlfield[@tag='001']" /></b></em><br/></xsl:template>
	<xsl:template name="Titre">
	<xsl:choose>
		<xsl:when test="(datafield[@tag='200']/subfield[@code='7'] = 'fa/r') or (datafield[@tag='200']/subfield[@code='7'] = 'ha/r')">
			<table border="0" cellspacing="0" callpadding="0" width="100%">				
					<xsl:call-template name="Tag_200_TD" />									
			</table>
		</xsl:when>
		<xsl:otherwise>
			<xsl:for-each select="datafield[@tag='200']">
				<xsl:call-template name="Tag_200" />
			</xsl:for-each>
			<br />
		</xsl:otherwise>
	</xsl:choose>
	</xsl:template>
	<xsl:template name="Tag_200_TD">
		<xsl:for-each select="datafield[@tag='200']">
			<xsl:choose>
				<xsl:when test="(subfield[@code='7'] = 'fa/r') or (subfield[@code='7'] = 'ha/r')"><tr><td valign="top" class="arabic"><xsl:call-template name="Tag_200_REORDERED" /></td></tr></xsl:when>
				<xsl:otherwise><tr><td valign="top"><xsl:call-template name="Tag_200" /></td></tr></xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_200">
		<b><xsl:value-of select="subfield[@code='a']" /></b>
		<b><xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">.&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		</b>
		<xsl:if test="subfield[@code='e'] != ''">&#32;:&#32;<xsl:value-of select="subfield[@code='e']" /></xsl:if>
		<xsl:if test="subfield[@code='b'] != ''">&#32;[<xsl:value-of select="subfield[@code='b']" />]</xsl:if>
		<xsl:if test="subfield[@code='f'] != ''">&#32;/&#32;<xsl:value-of select="subfield[@code='f']" /></xsl:if>
		<xsl:if test="subfield[@code='g'] != ''">&#32;;&#32;<xsl:value-of select="subfield[@code='g']" /></xsl:if>						
	</xsl:template>	
	<xsl:template name="Tag_200_REORDERED">
	<!-- same template with reordered subfields to balance the upcoming writing from right to left -->													
		<xsl:if test="subfield[@code='f'] != ''"><xsl:value-of select="subfield[@code='f']" />&#32;/&#32;</xsl:if>
		<xsl:if test="subfield[@code='g'] != ''"><xsl:value-of select="subfield[@code='g']" />&#32;;&#32;</xsl:if>		
		<xsl:if test="subfield[@code='b'] != ''">[<xsl:value-of select="subfield[@code='b']" />]&#32;</xsl:if>
		<xsl:if test="subfield[@code='e'] != ''"><xsl:value-of select="subfield[@code='e']" />&#32;:&#32;</xsl:if>				
		<b><xsl:if test="subfield[@code='i'] != ''"><xsl:value-of select="subfield[@code='i']" />.&#32;</xsl:if>
		<xsl:if test="subfield[@code='h'] != ''"><xsl:value-of select="subfield[@code='h']" />.&#32;</xsl:if></b>	
		<b><xsl:value-of select="subfield[@code='a']" /></b>		
	</xsl:template>
	<xsl:template name="Editions">
	<xsl:choose>
		<xsl:when test="(datafield[@tag='210']/subfield[@code='7'] = 'fa/r') or (datafield[@tag='210']/subfield[@code='7'] = 'ha/r')">
			<table border="0" cellspacing="0" callpadding="0" width="100%">				
					<xsl:call-template name="Tag_210_TD" />									
			</table>
		</xsl:when>
		<xsl:otherwise>
		<xsl:for-each select="datafield[@tag='210']">
			<xsl:call-template name="Tag_210" /><br/>
		</xsl:for-each><br/>
		</xsl:otherwise>
	</xsl:choose>			
	</xsl:template>
	<xsl:template name="Tag_210_TD">
		<xsl:for-each select="datafield[@tag='210']">
			<xsl:choose>
				<xsl:when test="(subfield[@code='7'] = 'fa/r') or (subfield[@code='7'] = 'ha/r')"><tr><td valign="top" class="arabic"><xsl:call-template name="Tag_210_REORDERED" /></td></tr></xsl:when>
				<xsl:otherwise><tr><td valign="top"><xsl:call-template name="Tag_210" /></td></tr></xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_210">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='c'] != ''">&#32;:&#32;<xsl:value-of select="subfield[@code='c']" /></xsl:if>
		<xsl:if test="subfield[@code='d'] != ''">,&#32;<xsl:value-of select="subfield[@code='d']" /></xsl:if>
	</xsl:template>
	<xsl:template name="Tag_210_REORDERED">
	<!-- same template with reordered subfields to balance the upcoming writing from right to left -->			
		<xsl:if test="subfield[@code='d'] != ''"><xsl:value-of select="subfield[@code='d']" />,&#32;</xsl:if>		
		<xsl:if test="subfield[@code='c'] != ''"><xsl:value-of select="subfield[@code='c']" />&#32;:&#32;</xsl:if>
		<xsl:value-of select="subfield[@code='a']" />
	</xsl:template>
	<xsl:template name="Issn"><b>Issn&nbsp;:&nbsp;</b><xsl:value-of select="datafield[@tag='011']/subfield[@code='a']" /></xsl:template>
	<xsl:template name="Url"><br/><b>Accès en ligne&nbsp;:&nbsp;</b>
		<xsl:for-each select="datafield[@tag='856']">
		<xsl:if test="subfield[@code='u'] != ''">&nbsp;<a><xsl:attribute name="href"><xsl:value-of select="subfield[@code='u']" /></xsl:attribute><xsl:value-of select="subfield[@code='u']" /></a></xsl:if>
		</xsl:for-each>		
	</xsl:template>	
	<xsl:template name="UrlExemplaire">
	<xsl:if test="datafield[@tag='856']/subfield[@code='u'] != ''">&nbsp;<a><xsl:attribute name="href"><xsl:value-of select="datafield[@tag='856']/subfield[@code='u']" /></xsl:attribute><xsl:value-of select="datafield[@tag='856']/subfield[@code='u']" /></a></xsl:if>
	</xsl:template>
	<xsl:template name="Supplément">
		<xsl:if test="datafield[@tag='421'] != ''"><br/><b>Supplément(s) : </b></xsl:if><xsl:for-each select="datafield[@tag='421']">
				<xsl:call-template name="Tag_421" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_421">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="PubMereSupplément">
		<xsl:if test="datafield[@tag='422'] != ''">
			<br/><b>Est le supplément de : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='422']">
			<xsl:call-template name="Tag_422" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_422">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="PubAvec">
		<xsl:if test="datafield[@tag='423'] != ''">
			<br/><b>Publié avec : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='423']">
				<xsl:call-template name="Tag_423" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_423">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="SuiteDe">
		<xsl:if test="datafield[@tag='430'] != ''">
			<br/><b>Suite de : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='430']">
			&#32;<xsl:call-template name="Tag_430" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_430">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="Succede">
		<xsl:if test="datafield[@tag='431'] != ''">
			<br/><b>Succède après scission à : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='431']">
			&#32;<xsl:call-template name="Tag_431" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_431">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="Remplace">
		<xsl:if test="datafield[@tag='432'] != ''">
			<br/><b>Remplace : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='432']">
			&#32;<xsl:call-template name="Tag_432" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_432">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="RemplacePart">
		<xsl:if test="datafield[@tag='433'] != ''">
			<br/><b>Remplace partiellement : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='433']">
			&#32;<xsl:call-template name="Tag_433" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_433">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="Absorbe">
		<xsl:if test="datafield[@tag='434'] != ''">
			<br/><b>Absorbe : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='434']">
			&#32;<xsl:call-template name="Tag_434" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_434">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="AbsorbePart">
		<xsl:if test="datafield[@tag='435'] != ''">
			<br/><b>Absorbe partiellement : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='435']">
			<xsl:call-template name="Tag_435" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_435">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="FusionDe">
		<xsl:if test="datafield[@tag='436'] != ''">
			<br/><b>Fusion de : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='436']">
			<xsl:call-template name="Tag_436" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_436">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="SuitePart">
		<xsl:if test="datafield[@tag='437'] != ''">
			<br/><b>Suite partielle de : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='437']">
			&#32;<xsl:call-template name="Tag_437" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_437">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="Devient">
		<xsl:if test="datafield[@tag='440'] != ''">
			<br/><b>Devient : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='440']">
			&#32;<xsl:call-template name="Tag_440" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_440">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="DevientPar">
		<xsl:if test="datafield[@tag='441'] != ''">
			<br/><b>Devient partiellement : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='441']">
			<xsl:call-template name="Tag_441" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_441">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="RemplacePar">
		<xsl:if test="datafield[@tag='442'] != ''">
			<br/><b>Remplacé par : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='442']">
			&#32;<xsl:call-template name="Tag_442" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_442">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="RemplacePartPar">
		<xsl:if test="datafield[@tag='443'] != ''">
			<br/><b>Remplacé partiellement par : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='443']">
			&#32;<xsl:call-template name="Tag_443" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_443">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="AbsorbePar">
		<xsl:if test="datafield[@tag='444'] != ''">
			<br/><b>Absorbé par : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='444']">
			<xsl:call-template name="Tag_444" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_444">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="AbsorbePartPar">
		<xsl:if test="datafield[@tag='445'] != ''">
			<br/><b>Absorbé partiellement par : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='445']">
			<xsl:call-template name="Tag_445" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_445">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="ScindeEn">
		<xsl:if test="datafield[@tag='446'] != ''">
			<br/><b>Scindé en : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='446']">
			<xsl:call-template name="Tag_446" />.&nbsp;
		</xsl:for-each>
		<xsl:if test="datafield[@tag='447'] != ''">
			<br/><b>Fusionne avec : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='447']">
		<xsl:if test="last() = position()">
			<br/><b>Pour donner : </b>
			</xsl:if>
			<xsl:call-template name="Tag_447" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_446">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="Tag_447">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="Redevient">
		<xsl:if test="datafield[@tag='448'] != ''">
			<br/><b>Redevient : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='448']">
			&#32;<xsl:call-template name="Tag_448" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_448">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="AutreEdition">
		<xsl:if test="datafield[@tag='451'] != ''">
			<br/><b>Autre édition sur le même support : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='451']">
			<xsl:call-template name="Tag_451" />.&nbsp;
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_451">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="AutreEditionAutreSupp">
		<xsl:if test="datafield[@tag='452'] != ''">
			<br/><b>Autre édition sur un autre support : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='452']">
			&#32;<xsl:call-template name="Tag_452" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_452">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="TraduitSousTitre">
		<xsl:if test="datafield[@tag='453'] != ''">
			<br/><b>Traduit sous le titre : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='453']">
			&#32;<xsl:call-template name="Tag_453" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_453">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="TraduitDe">
		<xsl:if test="datafield[@tag='454'] != ''">
			<br/><b>Traduit de : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='454']">
			&#32;<xsl:call-template name="Tag_454" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_454">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="ReproductionDe">
		<xsl:if test="datafield[@tag='455'] != ''">
			<br/><b>Reproduction de : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='455']">
			&#32;<xsl:call-template name="Tag_455" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_455">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="ReproduitComme">
		<xsl:if test="datafield[@tag='456'] != ''">
			<br/><b>Reproduit comme : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='456']">
			&#32;<xsl:call-template name="Tag_456" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_456">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>
	<xsl:template name="TitreCle"><br/><xsl:for-each select="datafield[@tag='530']"><b>Titre clé : </b><xsl:call-template name="Tag_530" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_530">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='b'] != ''"><xsl:text> </xsl:text><xsl:value-of select="subfield[@code='b']" /></xsl:if>
	</xsl:template>
	<xsl:template name="TitreAbrege">
		<xsl:if test="datafield[@tag='531'] != ''">
			<br/><b>Titre(s) abrégé(s) : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='531']"><xsl:text> </xsl:text><xsl:call-template name="Tag_531" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_531">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='b'] != ''"><xsl:text> </xsl:text><xsl:value-of select="subfield[@code='b']" /></xsl:if>
	</xsl:template>
	<xsl:template name="TitreParallele">
		<xsl:if test="datafield[@tag='510'] != ''">
			<br/><b>Titre parallèle : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='510']"><xsl:text> </xsl:text><xsl:call-template name="Tag_510" />
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="Tag_510">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='e'] != ''">:<xsl:value-of select="subfield[@code='e']" /></xsl:if>
	</xsl:template>
	<xsl:template name="TitreDeCouv">
		<xsl:if test="datafield[@tag='512'] != ''">
			<br/><b>Titre de couv. : </b>
		</xsl:if>
		<xsl:for-each select="datafield[@tag='512']"><xsl:text> </xsl:text><xsl:call-template name="Tag_512" />
		</xsl:for-each><br/>
	</xsl:template>
	<xsl:template name="Tag_512">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='e'] != ''">:<xsl:value-of select="subfield[@code='e']" /></xsl:if>
	</xsl:template>
	<xsl:template name="PublieEn"><xsl:call-template name="Tag_100" />,&#32;publié en <xsl:call-template name="Tag_101" /><xsl:call-template name="Tag_102" /><xsl:call-template name="Tag_110" />
	</xsl:template>
	<xsl:template name="Tag_100"><xsl:value-of select="substring(string(datafield[@tag='100']/subfield[@code='a']),10,4)" />-&nbsp;<xsl:value-of select="substring(string(datafield[@tag='100']/subfield[@code='a']),14,4)" />
	</xsl:template>
	<xsl:template name="Tag_101"><xsl:value-of select="datafield[@tag='101']/subfield[@code='a']" />
	</xsl:template>
	<xsl:template name="Tag_102">&nbsp;<xsl:value-of select="datafield[@tag='102']/subfield[@code='a']" />
	</xsl:template>
	<xsl:template name="Tag_110"><xsl:if test="datafield[@tag='110']/subfield[@code='a'] != ''">,&#32;&#32;
	<xsl:variable name="periodicite" select="substring(string(datafield[@tag='110']/subfield[@code='a']),2,1)" ></xsl:variable>
	<xsl:choose>
	<xsl:when test="$periodicite = 'a'">Quotidien</xsl:when>
	<xsl:when test="$periodicite = 'b'">Bihebdomadaire</xsl:when>
	<xsl:when test="$periodicite = 'c'">Hebdomadaire</xsl:when>
	<xsl:when test="$periodicite = 'd'">Toutes les deux semaines</xsl:when>
	<xsl:when test="$periodicite = 'e'">Deux fois par mois</xsl:when>
	<xsl:when test="$periodicite = 'f'">Mensuel</xsl:when>
	<xsl:when test="$periodicite = 'g'">Bimestriel</xsl:when>
	<xsl:when test="$periodicite = 'h'">Trimestriel</xsl:when>
	<xsl:when test="$periodicite = 'i'">Trois fois par an</xsl:when>
	<xsl:when test="$periodicite = 'j'">Semestriel</xsl:when>
	<xsl:when test="$periodicite = 'k'">Annuel</xsl:when>
	<xsl:when test="$periodicite = 'l'">Bisannuel</xsl:when>
	<xsl:when test="$periodicite = 'm'">Triennal</xsl:when>
	<xsl:when test="$periodicite = 'n'">Trois fois par semaine</xsl:when>
	<xsl:when test="$periodicite = 'o'">Trois fois par mois</xsl:when>
	<xsl:when test="$periodicite = 'p'">Mise à jour en continu</xsl:when>
	<xsl:when test="$periodicite = 'u'">Inconnue</xsl:when>
	<xsl:when test="$periodicite = 'y'">Sans périodicité</xsl:when>
	<xsl:when test="$periodicite = 'z'">Autre</xsl:when>
	</xsl:choose>	
	</xsl:if>
	</xsl:template>

	<xsl:template name="SourceCat"><br/>
		<xsl:if test="datafield[@tag='801'] != ''"><b>Source(s) de catalogage : </b></xsl:if><xsl:for-each select="datafield[@tag='801']"><xsl:if test="1 != position()">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_801" />
		</xsl:for-each>		
	</xsl:template>
	
	<xsl:template name="EtatLacune">
		<xsl:if test="datafield[@tag='959']/subfield[@code='r'] != ''">&nbsp;<xsl:value-of select="datafield[@tag='959']/subfield[@code='r']" /></xsl:if>
	</xsl:template>
	
	<xsl:template name="Tag_801"><xsl:if test="subfield[@code='b'] != ''"><xsl:value-of select="subfield[@code='b']" /></xsl:if>
	</xsl:template>
	
	<xsl:template name="Dewey"><br/>
		<xsl:if test="datafield[@tag='676'] != ''"><b>Dewey : </b></xsl:if><xsl:for-each select="datafield[@tag='676']"><xsl:if test="1 != position()">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_676" />
		</xsl:for-each>		
	</xsl:template>
	
	<xsl:template name="Tag_676"><xsl:if test="subfield[@code='a'] != ''"><xsl:value-of select="subfield[@code='a']" /></xsl:if>
	</xsl:template>
	
	<xsl:template name="PCP">
	<xsl:if test="datafield[@tag='930']/subfield[@code='z'] != ''">&nbsp;<xsl:value-of select="datafield[@tag='930']/subfield[@code='z']" /></xsl:if>
	</xsl:template>
	
	<xsl:template name="Localisations"><div style="text-align: center;"><table  style="margin: auto;" border="0" cellpadding="1" cellspacing="0" width="100%">
	<xsl:for-each select="datagroup[@tag='loc']">
	<xsl:choose>
		<xsl:when test="(position() mod 2 = 1)"><tr bgcolor="#dddddd"><xsl:call-template name="Tag_955" /><xsl:call-template name="Tag_930" /></tr></xsl:when>
		<xsl:otherwise><tr bgcolor="#bbbbbb"><xsl:call-template name="Tag_955" /><xsl:call-template name="Tag_930" /></tr></xsl:otherwise>
	</xsl:choose>		
		</xsl:for-each></table></div>
	</xsl:template>
	
	<xsl:template name="Tag_955">	
		<td width="80%" colspan="2" valign="top" align="left"><xsl:call-template name="Tag_955_inside" /></td>		
	</xsl:template>
	
	<xsl:template name="Tag_955_inside"><xsl:for-each select="datafield[@tag='955']"><xsl:value-of select="subfield[@code='r']" /></xsl:for-each>
		<xsl:if test="datafield[@tag='959']/subfield[@code='r'] != ''">&#32;<b>Lacunes : </b><xsl:for-each select="datafield[@tag='959']"><xsl:if test="./subfield[@code='r'] != ''">&nbsp;<xsl:value-of select="./subfield[@code='r']" /></xsl:if></xsl:for-each></xsl:if>
	</xsl:template>
	
	<xsl:template name="Tag_930"><td width="20%" align="right" valign="top"><xsl:for-each select="datafield[@tag='930']">
			<xsl:if test="(subfield[@code='a'] != '')">
				[<xsl:value-of select="subfield[@code='a']" />]
			</xsl:if><xsl:if test="subfield[@code='c'] != ''"><xsl:text> </xsl:text><xsl:value-of select="subfield[@code='c']" /></xsl:if>
				<xsl:if test="subfield[@code='d'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='d']" /></xsl:if>
				<xsl:if test="subfield[@code='l'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='l']" /></xsl:if>
				<xsl:if test="subfield[@code='e'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='e']" /></xsl:if>
		</xsl:for-each></td>
	</xsl:template>
	
	<!-- 
	<xsl:template name="Tag_916_inside">	
	<xsl:for-each select="datafield[@tag='916']">
			<xsl:if test="(subfield[@code='a'] != '')"><xsl:variable name="arg0" select="string(subfield[@code='a'])"/>
			Durée de conservation : <xsl:value-of select="Script:call($scriptInstance,'Traite916',$arg0)" />
			</xsl:if>
	</xsl:for-each>
	</xsl:template>
	 -->
	
</xsl:stylesheet>
