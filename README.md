# selfsudoc
Permet de réaliser et télécharger des catalogues de ressources continues en libre service

Ce projet inclue itextpdf 5.0.2 Core (com.itextpdf. voir https://github.com/itext/itextpdf/tree/5.0.2/itext/src/core/com/itextpdf/text ) sous license AGPL-3.0 ainsi que du code com.lowagie.text.rtf. version 4.2.0 (voir https://github.com/ymasory/iText-4.2.0/tree/master/src/rtf/com/lowagie/text/rtf)

Le module [Technic](https://github.com/abes-esr/selfsudoc/tree/develop/Technic) (`package fr.abes.derives.cli`) permet la modification d'une collection de `<record>` MARC XML, depuis leur forme originale ( notices de publications en série comportant plusieurs localisations et états de collection) vers un format "pivot" en XHTML ([exemple](/exemples/notices_315555245_1758704487056.xhtml)).

La version XHTML de la collection de notices servira ultérieurement à produire un équivalent dans des formats "bureautique" ( [RTF](https://github.com/abes-esr/selfsudoc/raw/refs/heads/develop/exemples/notices_315555245_1758704487056.rtf), [PDF](/exemples/notices_315555245_1758704487056.pdf), csv) du catalogue d'un seul centre de ressources du réseau Sudoc/PS (Publication en Série)
