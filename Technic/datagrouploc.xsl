<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:marc="http://www.loc.gov/MARC21/slim">

    <xsl:output method="xml" version="1.0" media-type="application/xml" encoding="UTF-8" indent="no"/>

    <!-- mémoriser les clés de niveau 1 pour recréér les nodesets plus tard -->
    <xsl:key name="datas-by-sample"
        match="datafield[((@tag='856' and subfield[@code='5'] != '') or @tag='316' and contains(subfield[@code='5'] , ':'))]"
        use="subfield[@code='5']"/>
    <xsl:key name="marc-datas-by-sample"
        match="marc:datafield[((@tag='856' and subfield[@code='5'] != '') or @tag='316' and contains(subfield[@code='5'] , ':'))]"
        use="marc:subfield[@code='5']"/>


    <xsl:template match="catalogue">
        <xsl:element name="catalogue">
            <xsl:apply-templates select="record|marc:record"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="record|marc:record">
        <xsl:element name="record" namespace="{namespace-uri()}">
            <xsl:apply-templates
                select="datafield[not (@tag='930' or @tag='917' or @tag='915' or @tag='955' or @tag='919' or @tag='931' or @tag='991' or @tag='992' or @tag='916' or @tag='959' or (@tag='856' and subfield[@code='5'] != '') or @tag='316')]|marc:datafield[not (@tag='930' or @tag='917' or @tag='915' or @tag='955' or @tag='919' or @tag='931' or @tag='991' or @tag='992' or @tag='916' or (@tag='856' and subfield[@code='5'] != '') or @tag='316')]|controlfield|marc:controlfield|leader|marc:leader"/>
            <xsl:for-each-group
                select="datafield[(@tag='930' or @tag='917' or @tag='915' or @tag='955' or @tag='919' or @tag='931' or @tag='991' or @tag='992' or @tag='916' or @tag='959' or (@tag='856' and subfield[@code='5' and string-length(text()) > 19]) or (@tag='316' and contains(subfield[@code='5'] , ':') and subfield[@code='5' and string-length(text()) > 19]) )]"
                group-by="subfield[@code='5']">
                <xsl:element name="datagroup" namespace="{namespace-uri()}">
                    <!-- surrounding tag -->
                    <xsl:attribute name="tag">loc</xsl:attribute>
                    <xsl:copy-of select="current-group()"/>
                    <xsl:variable name="pk" select="substring(current-grouping-key(),1,19)"/>
                    <!-- tronquer la cle courante -->
                    <xsl:copy-of select="key('datas-by-sample', $pk)"/>
                    <!-- y a t il des infos de niveaux 1 a recuperer ? -->
                </xsl:element>
            </xsl:for-each-group>
            <xsl:for-each-group
                select="marc:datafield[(@tag='930' or @tag='917' or @tag='915' or @tag='955' or @tag='919' or @tag='931' or @tag='991' or @tag='992' or @tag='916' or @tag='959' or (@tag='856' and subfield[@code='5' and string-length(text()) > 19]) or (@tag='316' and contains(subfield[@code='5'] , ':') and subfield[@code='5' and string-length(text()) > 19]) )]"
                group-by="marc:subfield[@code='5']">
                <xsl:element name="datagroup" namespace="{namespace-uri()}">
                    <!-- surrounding tag -->
                    <xsl:attribute name="tag">loc</xsl:attribute>
                    <xsl:copy-of select="current-group()"/>
                    <xsl:variable name="pk" select="substring(current-grouping-key(),1,19)"/>
                    <!-- tronquer la cle courante -->
                    <xsl:copy-of select="key('marc-datas-by-sample', $pk)"/>
                    <!-- y a t il des infos de niveaux 1 a recuperer ? -->
                </xsl:element>
            </xsl:for-each-group>
        </xsl:element>
    </xsl:template>

    <xsl:template match="datafield|marc:datafield|controlfield|marc:controlfield|leader|marc:leader">
        <xsl:copy-of select="."/>
    </xsl:template>

</xsl:stylesheet>
