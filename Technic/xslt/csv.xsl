<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:Script = "java:fr.abes.technic.RhinoScripting"
	exclude-result-prefixes="Script">
<xsl:output method="text" encoding="UTF-8" media-type="text/plain" indent="no" />

<xsl:param name="shortname" select="''"></xsl:param><!-- default is empty string -->
<xsl:param name="withcollections" select="''"></xsl:param><!-- default is empty string -->
<xsl:param name="runtimedir" select="''"></xsl:param><!-- default is empty string -->

	<xsl:variable name="scriptInstance" select="Script:new('/data/script.js','Cp1252')"></xsl:variable>
		
<!--  Technic private templates BEGIN -->		

		
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
			<xsl:if test="number($cx)=1 or number($cx)=4 or number($cx)=38"><!-- trick for PPN and ISSN and Dewey columns format -->
				<xsl:text>=</xsl:text>
			</xsl:if>
			<xsl:if test="number($cx)=40 and translate($valeur,'0123456789','')='()'"><!-- cas particulier pour éviter les nombres négatifs voir https://stp.abes.fr/node/18077/edit?origine=sudocpro -->
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
		<xsl:if test="number($cx)&lt;44">
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
			<xsl:with-param name="valeur" select="'Editions'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'4'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Issn'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'5'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Accès en ligne'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'6'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Supplément(s) : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'7'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Est le supplément de : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'8'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Publié avec : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'9'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Suite de : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'10'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Succède après scission à : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'11'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Remplace : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'12'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Remplace partiellement : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'13'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Absorbe : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'14'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Absorbe partiellement : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'15'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Fusion de : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'16'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Suite partielle de : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'17'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Devient : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'18'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Devient partiellement : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'19'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Remplacé par : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'20'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Remplacé partiellement par : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'21'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Absorbé par : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'22'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Absorbé partiellement par : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'23'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'ScindeEn'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'24'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Fusionne avec'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'25'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Redevient : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'26'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Autre édition sur le même support : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'27'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Autre édition sur un autre support : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'28'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Traduit sous le titre : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'29'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Traduit de : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'30'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Reproduction de : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'31'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Reproduit comme : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'32'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Titre clé : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'33'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Titre(s) abrégé(s) :	'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'34'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Titre parallèle : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'35'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Titre de couv. : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'36'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'PublieEn'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'37'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Périodicité'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'38'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Dewey : '" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'39'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Source(s) de catalogage : '" />
		</xsl:call-template>		
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'40'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'État de collection'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'41'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Lacunes'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'42'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Cote'" />
		</xsl:call-template>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'43'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'PCP'" />
		</xsl:call-template>		
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'44'" />
			<xsl:with-param name="cy" select="'1'" />
			<xsl:with-param name="valeur" select="'Note sur l exemplaire'" />
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
		<xsl:call-template name="Editions">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Issn">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Url">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>		
		<xsl:call-template name="Supplément">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="PubMereSupplément">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="PubAvec">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="SuiteDe">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Succede">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Remplace">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="RemplacePart">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Absorbe">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="AbsorbePart">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="FusionDe">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="SuitePart">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Devient">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="DevientPar">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="RemplacePar">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="RemplacePartPar">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="AbsorbePar">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="AbsorbePartPar">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="ScindeEn">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="FusionneAvec">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
	<xsl:call-template name="Redevient">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="AutreEdition">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="AutreEditionAutreSupp">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="TraduitSousTitre">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="TraduitDe">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="ReproductionDe">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="ReproduitComme">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="TitreCle">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="TitreAbrege">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="TitreParallele">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="TitreDeCouv">
		<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
	</xsl:call-template>		
		<xsl:call-template name="PublieEn">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Periodicite">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Dewey">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>		
		<xsl:call-template name="SourceCat">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>		
		<xsl:call-template name="Localisations">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Lacunes">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="Cote">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="PCP">
			<xsl:with-param name="ligne" select="$ligne"></xsl:with-param>
		</xsl:call-template>		
		<xsl:call-template name="Note">
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
			<xsl:value-of select="datafield[@tag='200']/subfield[(@code='a' and count(following-sibling::subfield[@code='classified-a'])=0) or (@code='classified-a')]" /><xsl:if test="datafield[@tag='200']/subfield[@code='h'] != ''">.&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='h']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='i'] != ''">.&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='i']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='e'] != ''">&#32;:&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='e']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='b'] != ''">&#32;[<xsl:value-of select="datafield[@tag='200']/subfield[@code='b']" />]</xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='f'] != ''">&#32;/&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='f']" /></xsl:if><xsl:if test="datafield[@tag='200']/subfield[@code='g'] != ''">&#32;;&#32;<xsl:value-of select="datafield[@tag='200']/subfield[@code='g']" /></xsl:if>
		</xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'2'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="Editions">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:choose>                                                                                                                                                        
    <xsl:when test="datafield[@tag='214']">	
	<xsl:for-each select="datafield[@tag='214']">
				<xsl:call-template name="Tag_210" />
			</xsl:for-each>	
	</xsl:when>                                                                      
    <xsl:otherwise>
    <xsl:for-each select="datafield[@tag='210']">
				<xsl:call-template name="Tag_210" />
			</xsl:for-each>       
    </xsl:otherwise>                             
</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'3'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="Tag_210">
		<xsl:value-of select="subfield[@code='a']" /><xsl:if test="subfield[@code='c'] != ''">&#32;:&#32;<xsl:value-of select="subfield[@code='c']" /></xsl:if><xsl:if test="subfield[@code='d'] != ''">,&#32;<xsl:value-of select="subfield[@code='d']" /></xsl:if>
	</xsl:template>

	<xsl:template name="Issn">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:value-of select="datafield[@tag='011']/subfield[@code='a']" />
		</xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'4'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>
	
	
	<xsl:template name="Url">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:call-template name="Tag_856" /></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'5'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_856">
		<xsl:for-each select="datafield[@tag='856']">
		<xsl:if test="subfield[@code='u'] != ''"><xsl:value-of select="subfield[@code='u']" /><xsl:text> </xsl:text></xsl:if>
		</xsl:for-each>
		<xsl:for-each select="datagroup[@tag='loc']">
		<xsl:if test="datafield[@tag='856']/subfield[@code='u'] != ''"><xsl:value-of select="datafield[@tag='856']/subfield[@code='u']" /><xsl:text> </xsl:text></xsl:if>
		</xsl:for-each>		
</xsl:template>

	<xsl:template name="Supplément">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:if test="datafield[@tag='421'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='421']"><xsl:call-template name="Tag_421" />.&#32;</xsl:for-each></xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'6'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="Tag_421">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>

	<xsl:template name="PubMereSupplément">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:if test="datafield[@tag='422'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='422']"><xsl:call-template name="Tag_422" />.&#32;</xsl:for-each></xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'7'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="Tag_422">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>

	<xsl:template name="PubAvec">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:if test="datafield[@tag='423'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='423']"><xsl:call-template name="Tag_423" />.&#32;</xsl:for-each></xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'8'" />
			<xsl:with-param name="cy" select="$ligne" />
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="Tag_423">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
	</xsl:template>

	<xsl:template name="SuiteDe">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v">
			<xsl:if test="datafield[@tag='430'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='430']">&#32;<xsl:call-template name="Tag_430" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'9'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_430">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="Succede">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='431'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='431']">&#32;<xsl:call-template name="Tag_431" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'10'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_431">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="Remplace">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='432'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='432']">&#32;<xsl:call-template name="Tag_432" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'11'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>				
</xsl:template>
<xsl:template name="Tag_432">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="RemplacePart">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='433'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='433']">&#32;<xsl:call-template name="Tag_433" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'12'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_433">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="Absorbe">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='434'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='434']">&#32;<xsl:call-template name="Tag_434" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'13'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_434">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="AbsorbePart">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='435'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='435']"><xsl:call-template name="Tag_435" />.&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'14'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>				
</xsl:template>
<xsl:template name="Tag_435">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="FusionDe">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='436'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='436']"><xsl:call-template name="Tag_436" />.&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'15'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_436">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="SuitePart">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='437'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='437']">&#32;<xsl:call-template name="Tag_437" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'16'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_437">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="Devient">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='440'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='440']">&#32;<xsl:call-template name="Tag_440" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'17'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_440">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="DevientPar">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='441'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='441']"><xsl:call-template name="Tag_441" />&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'18'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_441">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="RemplacePar">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='442'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='442']">&#32;<xsl:call-template name="Tag_442" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'19'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_442">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="RemplacePartPar">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='443'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='443']">&#32;<xsl:call-template name="Tag_443" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'20'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_443">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="AbsorbePar">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='444'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='444']"><xsl:call-template name="Tag_444" />.&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'21'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_444">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="AbsorbePartPar">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='445'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='445']"><xsl:call-template name="Tag_445" />.&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'22'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_445">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="ScindeEn">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:for-each select="datafield[@tag='446']"><xsl:call-template name="Tag_446" />.&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'23'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_446">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="FusionneAvec">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v">
<xsl:for-each select="datafield[@tag='447']"><xsl:if test="last() = position()">Pour donner :	</xsl:if><xsl:call-template name="Tag_447" />.&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'24'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_447">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>
	
<xsl:template name="Redevient">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='448'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='448']">&#32;<xsl:call-template name="Tag_448" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'25'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_448">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="AutreEdition">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='451'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='451']"><xsl:call-template name="Tag_451" />.&#32;</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'26'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_451">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="AutreEditionAutreSupp">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='452'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='452']">&#32;<xsl:call-template name="Tag_452" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'27'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_452">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="TraduitSousTitre">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='453'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='453']">&#32;<xsl:call-template name="Tag_453" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'28'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_453">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="TraduitDe">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='454'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='454']">&#32;<xsl:call-template name="Tag_454" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'29'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_454">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="ReproductionDe">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='455'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='455']">&#32;<xsl:call-template name="Tag_455" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'30'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_455">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="ReproduitComme">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='456'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='456']">&#32;<xsl:call-template name="Tag_456" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'31'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_456">
		<xsl:value-of select="subfield[@code='t']" />
		<xsl:if test="subfield[@code='x'] != ''">,&#32;<xsl:value-of select="subfield[@code='x']" /></xsl:if>	
		<xsl:if test="subfield[@code='y'] != ''">,&#32;<xsl:value-of select="subfield[@code='y']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,&#32;<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
</xsl:template>

<xsl:template name="TitreCle">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:for-each select="datafield[@tag='530']"><xsl:call-template name="Tag_530" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'32'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_530">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='b'] != ''">&#32;<xsl:value-of select="subfield[@code='b']" /></xsl:if>
</xsl:template>

<xsl:template name="TitreAbrege">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='531'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='531']">&#32;<xsl:call-template name="Tag_531" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'33'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_531">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='b'] != ''">&#32;<xsl:value-of select="subfield[@code='b']" /></xsl:if>
</xsl:template>

<xsl:template name="TitreParallele">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='510'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='510']">&#32;<xsl:call-template name="Tag_510" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'34'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_510">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='e'] != ''">:<xsl:value-of select="subfield[@code='e']" /></xsl:if>
</xsl:template>

<xsl:template name="TitreDeCouv">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:if test="datafield[@tag='512'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='512']">&#32;<xsl:call-template name="Tag_512" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'35'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
<xsl:template name="Tag_512">
		<xsl:value-of select="subfield[@code='a']" />
		<xsl:if test="subfield[@code='h'] != ''">.&#32;<xsl:value-of select="subfield[@code='h']" /></xsl:if>
		<xsl:if test="subfield[@code='i'] != ''">,<xsl:value-of select="subfield[@code='i']" /></xsl:if>
		<xsl:if test="subfield[@code='e'] != ''">:<xsl:value-of select="subfield[@code='e']" /></xsl:if>
</xsl:template>

<xsl:template name="PublieEn">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:call-template name="Tag_100" />,&#32;publié en <xsl:call-template name="Tag_101" />,&#32;<xsl:call-template name="Tag_102" /></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'36'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>
</xsl:template>


	<xsl:template name="Tag_100"><xsl:value-of select="substring(string(datafield[@tag='100']/subfield[@code='a']),10,4)" />-&#32;<xsl:value-of select="substring(string(datafield[@tag='100']/subfield[@code='a']),14,4)" />
	</xsl:template>
	<xsl:template name="Tag_101"><xsl:value-of select="datafield[@tag='101']/subfield[@code='a']" />
	</xsl:template>
	<xsl:template name="Tag_102"><xsl:value-of select="datafield[@tag='102']/subfield[@code='a']" />
	</xsl:template>

<xsl:template name="Periodicite">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:call-template name="Tag_110" /></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'37'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>
</xsl:template>
	

	<xsl:template name="Tag_110"><xsl:if test="datafield[@tag='110']/subfield[@code='a'] != ''">
    <xsl:variable name="perio" select="substring(string(datafield[@tag='110']/subfield[@code='a']),2,1)" ></xsl:variable>
    <xsl:choose>
    <xsl:when test="$perio = 'a'">Quotidien</xsl:when>
    <xsl:when test="$perio = 'b'">Bihebdomadaire</xsl:when>
    <xsl:when test="$perio = 'c'">Hebdomadaire</xsl:when>
    <xsl:when test="$perio = 'd'">Toutes les deux semaines</xsl:when>
    <xsl:when test="$perio = 'e'">Deux fois par mois</xsl:when>
    <xsl:when test="$perio = 'f'">Mensuel</xsl:when>
    <xsl:when test="$perio = 'g'">Bimestriel</xsl:when>
    <xsl:when test="$perio = 'h'">Trimestriel</xsl:when>
    <xsl:when test="$perio = 'i'">Trois fois par an</xsl:when>
    <xsl:when test="$perio = 'j'">Semestriel</xsl:when>
    <xsl:when test="$perio = 'k'">Annuel</xsl:when>
    <xsl:when test="$perio = 'l'">Bisannuel</xsl:when>
    <xsl:when test="$perio = 'm'">Triennal</xsl:when>
    <xsl:when test="$perio = 'n'">Trois fois par semaine</xsl:when>
    <xsl:when test="$perio = 'o'">Trois fois par mois</xsl:when>
    <xsl:when test="$perio = 'p'">Mise à jour en continu</xsl:when>
    <xsl:when test="$perio = 'u'">Inconnue</xsl:when>
    <xsl:when test="$perio = 'y'">Sans périodicité</xsl:when>
    <xsl:when test="$perio = 'z'">Autre</xsl:when>
    </xsl:choose>   
    </xsl:if>
    </xsl:template>

<xsl:template name="Dewey">
<xsl:param name="ligne"></xsl:param>	
<xsl:variable name="v"><xsl:if test="datafield[@tag='676'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='676']"><xsl:if test="1 != position()">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_676" />
		</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'38'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>				
</xsl:template>

<xsl:template name="Tag_676"><xsl:if test="subfield[@code='a'] != ''"><xsl:value-of select="subfield[@code='a']" /></xsl:if>
	</xsl:template>
	
<xsl:template name="SourceCat">
<xsl:param name="ligne"></xsl:param>	
<xsl:variable name="v"><xsl:if test="datafield[@tag='801'] != ''"></xsl:if><xsl:for-each select="datafield[@tag='801']"><xsl:if test="1 != position()">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_801" />
		</xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'39'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>				
</xsl:template>

<xsl:template name="Tag_801"><xsl:if test="subfield[@code='b'] != ''"><xsl:value-of select="subfield[@code='b']" /></xsl:if>
	</xsl:template>
	
	
<xsl:template name="Localisations">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v"><xsl:for-each select="datagroup[@tag='loc']"><xsl:if test= "(position() > 1 )">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_955r" /></xsl:for-each></xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'40'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>
	
	<xsl:template name="Tag_955r"><xsl:for-each select="datafield[@tag='955']"><xsl:value-of select="subfield[@code='r']" /></xsl:for-each></xsl:template>
	
<xsl:template name="Lacunes">
		<xsl:param name="ligne"></xsl:param>
		<xsl:variable name="v"><xsl:for-each select="datagroup[@tag='loc']"><xsl:if test= "(position() > 1 )">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_959r" /></xsl:for-each></xsl:variable>
		<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'41'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
		</xsl:call-template>		
</xsl:template>	
	
	<xsl:template name="Tag_959r"><xsl:for-each select="datafield[@tag='959']"><xsl:value-of select="subfield[@code='r']" /></xsl:for-each></xsl:template>
	
	
	<!-- 
	<xsl:template name="Tag_916"><xsl:for-each select="datafield[@tag='916']">
			<xsl:if test="(subfield[@code='a'] != '')"><xsl:variable name="arg0" select="string(subfield[@code='a'])"/>Durée de conservation : <xsl:value-of select="Script:call($scriptInstance,'Traite916',$arg0)" /></xsl:if>
		</xsl:for-each>
	</xsl:template>
 -->
	
<xsl:template name="Cote">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v">
	<xsl:for-each select="datagroup[@tag='loc']"><xsl:if test= "(position() > 1 )">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_930" /></xsl:for-each>
</xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'42'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>

<xsl:template name="Tag_930"><xsl:for-each select="datafield[@tag='930']"><xsl:if test="(subfield[@code='a'] != '')">&#32;[<xsl:value-of select="subfield[@code='a']" />]</xsl:if><xsl:if test="subfield[@code='c'] != ''"><xsl:text> </xsl:text><xsl:value-of select="subfield[@code='c']" /></xsl:if><xsl:if test="subfield[@code='d'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='d']" /></xsl:if><xsl:if test="subfield[@code='l'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='l']" /></xsl:if><xsl:if test="subfield[@code='e'] != ''">.<xsl:text> </xsl:text><xsl:value-of select="subfield[@code='e']" /></xsl:if></xsl:for-each></xsl:template>


<xsl:template name="PCP">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v">
	<xsl:if test="datagroup[@tag='loc']/datafield[@tag='930']/subfield[@code='z'] != ''"><!--  at least one PCP code -->
		<xsl:for-each select="datagroup[@tag='loc']"><xsl:call-template name="Tag_930z"><xsl:with-param name="pos" select="position()"/></xsl:call-template></xsl:for-each>
	</xsl:if>
	</xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'43'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>		
</xsl:template>	

	<xsl:template name="Tag_930z">
	<xsl:param name="pos"></xsl:param>
	<xsl:if test= "($pos > 1 )">&#32;*&#32;</xsl:if>
	<xsl:for-each select="datafield[@tag='930']"><xsl:value-of select="subfield[@code='z']" /></xsl:for-each>
	</xsl:template>
	
	
	


<xsl:template name="Note">
<xsl:param name="ligne"></xsl:param>
<xsl:variable name="v">
<xsl:if test="(datagroup[@tag='loc']/datafield[@tag='316']/subfield[@code='a'] != '') and (datagroup[@tag='loc']/datafield[@tag='930']/subfield[@code='z'] != '')"><!-- only if belong to PCP -->
	<xsl:for-each select="datagroup[@tag='loc']"><xsl:if test= "(position() > 1 )">&#32;*&#32;</xsl:if><xsl:call-template name="Tag_316" /></xsl:for-each>
</xsl:if>
</xsl:variable>
<xsl:call-template name="donnee">
			<xsl:with-param name="cx" select="'44'" />
			<xsl:with-param name="cy" select="$ligne" />			
			<xsl:with-param name="valeur" select="$v" />
</xsl:call-template>

</xsl:template>
<xsl:template name="Tag_316">				
		<xsl:if test="datafield[@tag='316']/subfield[@code='a'] != ''"><xsl:value-of select="datafield[@tag='316']/subfield[@code='a']" /></xsl:if>		
</xsl:template>

	


</xsl:stylesheet>