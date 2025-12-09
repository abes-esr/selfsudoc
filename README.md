# selfsudoc
Permet de réaliser et télécharger des catalogues de ressources continues en libre service

Ce projet inclue itextpdf 5.0.2 Core (com.itextpdf. voir https://github.com/itext/itextpdf/tree/5.0.2/itext/src/core/com/itextpdf/text ) sous license AGPL-3.0 ainsi que du code com.lowagie.text.rtf. version 4.2.0 (voir https://github.com/ymasory/iText-4.2.0/tree/master/src/rtf/com/lowagie/text/rtf)

Le module [Technic](https://github.com/abes-esr/selfsudoc/tree/develop/Technic) (`package fr.abes.derives.cli`) permet la modification d'une collection de `<record>` MARC XML, depuis leur forme [originale](https://github.com/abes-esr/selfsudoc/raw/refs/heads/develop/exemples/notices_315555245_1758801826559.xml) ( notices de publications en série comportant plusieurs localisations et états de collection) vers un format "pivot" en XHTML ([exemple](/exemples/notices_315555245_1758704487056.xhtml)).

La version XHTML de la collection de notices servira ultérieurement en entrée du module [Renderer](https://github.com/abes-esr/selfsudoc/tree/develop/Renderer) pour produire en sortie un équivalent dans des formats "bureautique" ( [RTF](https://github.com/abes-esr/selfsudoc/raw/refs/heads/develop/exemples/notices_315555245_1758704487056.rtf), [PDF](/exemples/notices_315555245_1758704487056.pdf), csv) du catalogue d'un seul centre de ressources du réseau Sudoc/PS (Publication en Série)

## Restauration
      
### Restauration de la base de données Oracle

- se connecter avec le compte oracle sur ononis (machine Oracle) :
 
- Récupérer la sauvegarde depuis sotora : 
```bash
rsync --progress -av devel@socorro.v104.abes.fr:/backup_pool/ononis-prod-dumps/daily.0/racine/backup-sql/ABES/PRODUITSDERIVES/dumpPRODUITS_DERIVES.dmp /backup-sql/ABES/PRODUITSDERIVES/dump.dmp
```
*Pour sélectionner une sauvegarde autre que la plus récente, il suffit de remplacer daily.0 dans la commande par le jour souhaité (daily.1 pour la veille, daily.2 pour l'avant-veille, etc.)*

- Puis lancer les commandes suivantes pour importer le dump dans la base de données :

```bash
export NLS_LANG=AMERICAN_AMERICA.UTF8
export ORACLE_SID='ABES'
setsid impdp \'/ as sysdba\' SCHEMAS=PRODUITS_DERIVES TABLE_EXISTS_ACTION=REPLACE dumpfile='dump.dmp' logfile=importProduitsDerives.log directory=DPDUMP_PRODUITSDERIVES
```
Si le directory object n'existe pas dans la base de données, il doit être ajouté via : 
```bash
CREATE DIRECTORY DPDUMP_PRODUITSDERIVES AS '/backup-sql/ABES/PRODUITSDERIVES';
```
_Procédure executée avec succès sur le test le 08/12/2025_
