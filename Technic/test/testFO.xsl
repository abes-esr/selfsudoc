<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo = "http://www.w3.org/1999/XSL/Format"
                exclude-result-prefixes="fo">


                
<xsl:output method="xml"
            version="1.0"
            media-type="application/xml"
            encoding="UTF-8"
            indent="no" />


	<xsl:template match="/">	
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4"
        margin-left   = "5mm"
              margin-right  = "5mm"
              margin-bottom = "5mm"
              margin-top    = "5mm"
              page-width    = "210mm"
              page-height   = "297mm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="A4">

	<fo:flow flow-name="xsl-region-body" font-family="serif" font-size="8pt">
		<fo:block>Liste Des Notices Complete</fo:block>
		
		
		
		<xsl:for-each select="/catalogue/record">
		
		<fo:list-block provisional-distance-between-starts="30mm" 
            provisional-label-separation="5mm">
			
		      <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">PPN</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:value-of select="./controlfield[@tag='001']"></xsl:value-of></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Titre</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:value-of select="./datafield[@tag='200']/subfield[@code='y']"></xsl:value-of>
			.&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='h']"></xsl:value-of>
			.&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='i']"></xsl:value-of>
			&#32;:&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='e']"></xsl:value-of>
			&#32;[<xsl:value-of select="./datafield[@tag='200']/subfield[@code='b']"></xsl:value-of>]
			&#32;/&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='f']"></xsl:value-of>			
			&#32;;&#32;<xsl:value-of select="./datafield[@tag='200']/subfield[@code='g']"></xsl:value-of></fo:block>
		  </fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Editions</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:value-of select="./datafield[@tag='210']/subfield[@code='a']"></xsl:value-of>
			&#32;:&#32;<xsl:value-of select="./datafield[@tag='210']/subfield[@code='c']"></xsl:value-of>
			,&#32;<xsl:value-of select="./datafield[@tag='210']/subfield[@code='d']"></xsl:value-of></fo:block>
		  </fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Issn</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:value-of select="./datafield[@tag='011']/subfield[@code='a']"></xsl:value-of></fo:block>
		  </fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Supplément</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='421']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">PubMereSupplément</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='422']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">PubAvec</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='423']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">SuiteDe</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='430']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Succede</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='431']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Remplace</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='432']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">RemplacePart</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='433']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Absorbe</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='434']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">AbsorbePart</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='435']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">FusionDe</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='436']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">SuitePart</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='437']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Devient</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='440']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">DevientPar</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='441']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">RemplacePar</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='442']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">RemplacePartPar</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='443']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Absorbe</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='444']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">AbsorbePar</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='445']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">ScindeEn</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X</fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">FusionneAvec</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X</fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">PourDonner</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X</fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Redevient</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='448']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">AutreEdition</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='451']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">AutreEditionAutreSupp</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='452']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">TraduitSousTitre</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='453']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">TraduitDe</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='454']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">ReproductionDe</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='455']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">ReproduitComme</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='456']">
                *&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				,*&#32;<xsl:value-of select="./subfield[@code='x']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">TitreCle</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='530']">
                <xsl:value-of select="./subfield[@code='y']"></xsl:value-of>
				&#32;<xsl:value-of select="./subfield[@code='b']"></xsl:value-of>				
			  </xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">TitreAbrege</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='531']">
                <xsl:value-of select="./subfield[@code='y']"></xsl:value-of>
				&#32;<xsl:value-of select="./subfield[@code='b']"></xsl:value-of>				
			  </xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">TitreParallele</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='510']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				:<xsl:value-of select="./subfield[@code='e']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">TitreDeCouv</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='512']">
                .*&#32;<xsl:value-of select="./subfield[@code='h']"></xsl:value-of>
				,<xsl:value-of select="./subfield[@code='i']"></xsl:value-of>
				:<xsl:value-of select="./subfield[@code='e']"></xsl:value-of>
				</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">PublieEn</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:value-of select="./datafield[@tag='100']/subfield[@code='a']"></xsl:value-of>
			,&#32;en&#32;<xsl:value-of select="./datafield[@tag='101']/subfield[@code='a']"></xsl:value-of>
			&#32;<xsl:value-of select="./datafield[@tag='102']/subfield[@code='a']"></xsl:value-of>
			,&#32;<xsl:value-of select="./datafield[@tag='110']/subfield[@code='z']"></xsl:value-of></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">SourceCat</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='801']">
			<fo:block>&#32;*&#32;<xsl:value-of select="./subfield[@code='b']"></xsl:value-of></fo:block>
			</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Rcr</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:value-of select="./datafield[@tag='930']/subfield[@code='b'][1]"></xsl:value-of></fo:block></fo:list-item-body></fo:list-item>
              <fo:list-item relative-align="baseline"><fo:list-item-label text-align="end" start-indent="5mm" end-indent="label-end()">
                <fo:block font-weight="bold">Localisations</fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"><fo:block>X<xsl:for-each select="./datafield[@tag='955']/*[name()='subfield']">
		    <fo:block><xsl:value-of select="@code"></xsl:value-of><xsl:value-of select="."></xsl:value-of></fo:block>
		    </xsl:for-each>
		    <xsl:for-each select="./datafield[@tag='930']">
			<fo:block>&#32;[<xsl:value-of select="./subfield[@code='a']"></xsl:value-of>]</fo:block>
			</xsl:for-each>
			<xsl:for-each select="./datafield[@tag='916']">
			<fo:block>Durée de conservation : <xsl:value-of select="./subfield[@code='a']"></xsl:value-of></fo:block>
			</xsl:for-each></fo:block></fo:list-item-body></fo:list-item>
		
		
		
		
		
        
        
        </fo:list-block>
        
        </xsl:for-each>
        
        		
		
				
	</fo:flow>
</fo:page-sequence>
</fo:root>
</xsl:template>
	
		
	
</xsl:stylesheet>