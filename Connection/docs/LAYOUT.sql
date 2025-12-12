--------------------------------------------------------
--  Fichier créé - mardi-septembre-23-2025   
--------------------------------------------------------
REM
INSERTING into PRODUITS_DERIVES.LAYOUT
SET DEFINE OFF;
Insert into PRODUITS_DERIVES.LAYOUT (LIBELLE, XSL_STYLESHEET, OUTPUT_FORMAT)
values ('Catalogue simplifié', 'xslt/csvabrg.xsl', 'slk');
Insert into PRODUITS_DERIVES.LAYOUT (LIBELLE, XSL_STYLESHEET, OUTPUT_FORMAT)
values ('Liste PPN', 'xslt/csvppn.xsl', 'slk');
Insert into PRODUITS_DERIVES.LAYOUT (LIBELLE, XSL_STYLESHEET, OUTPUT_FORMAT)
values ('Catalogue complet', 'xslt/ABES_XHTML.xsl', 'pdfrtf');
Insert into PRODUITS_DERIVES.LAYOUT (LIBELLE, XSL_STYLESHEET, OUTPUT_FORMAT)
values ('Catalogue complet', 'xslt/csv.xsl', 'slk');
