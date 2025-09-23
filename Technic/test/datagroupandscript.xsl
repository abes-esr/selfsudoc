<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:Script = "java:fr.abes.technic.RhinoScripting"
                exclude-result-prefixes="Script">

<xsl:output method="html"
            version="4.01"
            encoding="UTF-8"
            doctype-public="-//W3C//DTD HTML 4.01//EN"
            doctype-system="http://www.w3.org/TR/html4/strict.dtd" />
            

	<xsl:template match="/">
	
	<xsl:variable name="scriptInstance" select="Script:new('script.js','Cp1252')"/>
	<xsl:choose>
          <xsl:when test="function-available('Script:evaluate')">
          <!-- Java extension found -->
          <xsl:value-of select="Script:evaluate($scriptInstance,1)"/>
          <!-- script evaluated from line number 1-->
          </xsl:when>
          <xsl:otherwise>function is not available</xsl:otherwise>
        </xsl:choose>
        
        
		<html>
			<head>
			<title>LISTE ALPHABETIQUE DES PUBLICATIONS EN SERIE</title></head>
			<body>
			<div align="center">LISTE ALPHABETIQUE DES PUBLICATIONS EN SERIE </div>
			<div align="center">RCR N°<xsl:value-of select="//datagroup[@tag='loc']/datafield[@tag='930']/subfield[@code='b']"></xsl:value-of></div>
			<xsl:for-each select="/catalogue/record">
					<div>
					<b>PPN</b> <xsl:value-of select="./controlfield[@tag='001']"></xsl:value-of><br/>
					<b>Titre</b> <xsl:value-of select="./datafield[@tag='200']/subfield[@code='y']"></xsl:value-of>
			.&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='h']"></xsl:value-of>
			.&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='i']"></xsl:value-of>
			&#32;:&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='e']"></xsl:value-of>
			&#32;[<xsl:value-of select="./datafield[@tag='200']/subfield[@code='b']"></xsl:value-of>]
			&#32;/&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='f']"></xsl:value-of>			
			&#32;;&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='g']"></xsl:value-of><br/>
			<b>Editions</b> <xsl:value-of select="./datafield[@tag='210']/subfield[@code='a']"></xsl:value-of>
			&#32;:&#32;<xsl:value-of select="./datafield[@tag='210']/subfield[@code='c']"></xsl:value-of>
			,&#32;<xsl:value-of select="./datafield[@tag='210']/subfield[@code='d']"></xsl:value-of><br/>
			<b>Issn</b> <xsl:value-of select="./datafield[@tag='011']/subfield[@code='a']"></xsl:value-of><br/>
			<b>Supplément</b> <xsl:for-each select="./datafield[@tag='421']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>PubMereSupplément</b> <xsl:for-each select="./datafield[@tag='422']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>PubAvec</b> <xsl:for-each select="./datafield[@tag='423']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>SuiteDe</b> <xsl:for-each select="./datafield[@tag='430']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>Succede</b> <xsl:for-each select="./datafield[@tag='431']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>Remplace</b> <xsl:for-each select="./datafield[@tag='432']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>RemplacePart</b> <xsl:for-each select="./datafield[@tag='433']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>Absorbe</b> <xsl:for-each select="./datafield[@tag='434']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>AbsorbePart</b> <xsl:for-each select="./datafield[@tag='435']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>FusionDe</b> <xsl:for-each select="./datafield[@tag='436']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>SuitePart</b> <xsl:for-each select="./datafield[@tag='437']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>Devient</b> <xsl:for-each select="./datafield[@tag='440']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>DevientPar</b> <xsl:for-each select="./datafield[@tag='441']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>RemplacePar</b> <xsl:for-each select="./datafield[@tag='442']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>RemplacePartPar</b> <xsl:for-each select="./datafield[@tag='443']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>Absorbe</b> <xsl:for-each select="./datafield[@tag='444']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>AbsorbePar</b> <xsl:for-each select="./datafield[@tag='445']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>ScindeEn</b> <br/>
			<b>FusionneAvec</b><br/>
			<b>PourDonner</b><br/>
			<b>Redevient</b> <xsl:for-each select="./datafield[@tag='448']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>AutreEdition</b> <xsl:for-each select="./datafield[@tag='451']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>AutreEditionAutreSupp</b> <xsl:for-each select="./datafield[@tag='452']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>TraduitSousTitre</b> <xsl:for-each select="./datafield[@tag='453']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>TraduitDe</b> <xsl:for-each select="./datafield[@tag='454']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>ReproductionDe</b> <xsl:for-each select="./datafield[@tag='455']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>ReproduitComme</b> <xsl:for-each select="./datafield[@tag='456']">
                *&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>TitreCle</b> <xsl:for-each select="./datafield[@tag='530']">
                <xsl:value-of select="./subfield[@code='y']"></xsl:value-of>
				&#32;<xsl:value-of select="./subfield[@code='b']"></xsl:value-of>				
			  </xsl:for-each><br/>
			 <b>TitreAbrege</b> <xsl:for-each select="./datafield[@tag='531']">
                <xsl:value-of select="./subfield[@code='y']"></xsl:value-of>
				&#32;<xsl:value-of select="./subfield[@code='b']"></xsl:value-of>				
			  </xsl:for-each><br/>
			 <b>TitreParallele</b> <xsl:for-each select="./datafield[@tag='510']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				:<xsl:value-of select="./subfield[@code='e']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>TitreDeCouv</b> <xsl:for-each select="./datafield[@tag='512']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				:<xsl:value-of select="./subfield[@code='e']"></xsl:value-of>
				</xsl:for-each><br/>
			<b>PublieEn</b> <xsl:value-of select="./datafield[@tag='100']/subfield[@code='a']"></xsl:value-of>
			,&#32;en&#32;<xsl:value-of select="./datafield[@tag='101']/subfield[@code='a']"></xsl:value-of>
			&#32;<xsl:value-of select="./datafield[@tag='102']/subfield[@code='a']"></xsl:value-of>
			,&#32;<xsl:value-of select="./datafield[@tag='110']/subfield[@code='z']"></xsl:value-of><br/>
			<b>SourceCat</b> <div><xsl:for-each select="./datafield[@tag='801']">
			&#32;*&#32;<xsl:value-of select="./subfield[@code='b']"></xsl:value-of><br/>
			</xsl:for-each></div><br/>		
        <b>Localisations</b><table border="0" >
		<xsl:for-each select="./datagroup[@tag='loc']">
		<tr>&#32;<td>
		<xsl:for-each select="./datafield[@tag='955']">
		<font size="2">
			<xsl:if test= "(position() > 1 )">
			;
			</xsl:if>
			<xsl:value-of select="Script:call($scriptInstance,'Traite955Init')" />
			<xsl:for-each select="child::*">
                          <xsl:variable name="arg0" select="string(@code)"/>
                          <xsl:variable name="arg1" select="string(.)"/>
                          <xsl:value-of select="Script:call($scriptInstance,'Traite955Add',$arg0,$arg1)" />
			</xsl:for-each>
			<xsl:value-of select="Script:call($scriptInstance,'Traite955')" />			
		</font>
		</xsl:for-each>
		</td>
		<td></td><td>
		<font size="2">
		<xsl:for-each select="./datafield[@tag='916']">
			<xsl:if test="(subfield[@code='a'] != '')">
                        <xsl:variable name="arg0" select="string(subfield[@code='a'])"/>
			Durée de conservation : <xsl:value-of select="Script:call($scriptInstance,'Traite916',$arg0)" />
			</xsl:if>
		</xsl:for-each>
		</font>
		</td>
		<td width = "50"></td><td>
		<font size="2">
		<xsl:for-each select="./datafield[@tag='930']">
			<xsl:if test="(subfield[@code='a'] != '')">
				[<xsl:value-of select="subfield[@code='a']" />]
			</xsl:if>
		</xsl:for-each>
		</font>
		</td>
		</tr>
		</xsl:for-each>
		</table>


</div>
</xsl:for-each>
</body>
</html>
</xsl:template>
</xsl:stylesheet>