/**
 * Par ECT
 * Permet d'insérer un bandeau de cookie sur un site WEB.
 * L'ensemble des paramètres est paramétrables à travers des attributs ajoutés au code à mettre sur le site WEB (cf. exemple ci-dessous).
 * Une fois l'utilisateur ayant fermé le bandeau, un cookie de 13 mois est enregistré pour le site. Le bandeau ne s'affiche plus.
 *
 * Voici le code à insérer dans vos site WEB:
 * <script src="js/bandeau.js" type="text/javascript" id="cookie-banner-script"></script>
 *
 * Pour modifier un paramètre, <script src="js/bandeau.js" type="text/javascript" id="cookie-banner-script" data-mon-parametre="sa-valeur"></script>
 * data-cookie-banner-style : le style de la bannière
 * data-cookie-banner-style-text : le style du texte dans la bannière
 * data-cookie-banner-style-more : le style du lien "En savoir plus >"
 * data-cookie-banner-style-accept : le style du bouton ok, fermer
 * data-cookie-banner-style-decline : le style du bouton ok
 *
 * data-cookie-banner-text : le texte affiché
 * data-cookie-banner-text-more : le texte affiché pour le bouton more
 * data-cookie-banner-text-accept : le texte affiché dans le bouton ok
 * data-cookie-banner-text-decline : le texte affiché dans le bouton refuser
 *
 * data-cookie-banner-element-id : l'id positionné dans le div de la bannière
 * data-cookie-banner-url : l'url derrière le bouton "More"
 *
 * data-cookie-banner-decline : affiche un deuxième pourtant pour refuser
 */

function launchCookieBannerScript() {
    var cookieBannerScript = document.getElementById("cookie-banner-script");

    if (cookieBannerScript == null) {
        console.error("Can't find the cookie-banner-script");
    } else if (getCookie("cookie-banner-accept") !== "1" && getCookie("cookie-banner-decline") !== "1") {
        var cookieBannerStyle = checkParams(cookieBannerScript, "data-cookie-banner-style", "z-index: 10000; background-color: black; position: absolute; width: 99%; top: 0; padding: 0.5%; color: white;");
        var cookieBannerStyleText = checkParams(cookieBannerScript, "data-cookie-banner-style-text", "position: relative; top: 5px;");
        var cookieBannerStyleMore = checkParams(cookieBannerScript, "data-cookie-banner-style-more", "color: white; text-decoration: underline;");
        var cookieBannerStyleAccept = checkParams(cookieBannerScript, "data-cookie-banner-style-accept", "margin-right: 20px; float: right; border-radius: 10px; background: #4596ec; color: #fff; padding: 5px 10px; text-decoration: none;");
        var cookieBannerStyleDecline = checkParams(cookieBannerScript, "data-cookie-banner-style-decline", "margin-right: 20px; float: right; border-radius: 10px; background: #4596ec; color: #fff; padding: 5px 10px; text-decoration: none;");

        var cookieBannerText = checkParams(cookieBannerScript, "data-cookie-banner-text", "En poursuivant votre navigation sur ce site, vous acceptez l’utilisation de cookies ou autres traceurs.");
        var cookieBannerTextMore = checkParams(cookieBannerScript, "data-cookie-banner-text-more", "En savoir plus >");
        var cookieBannerTextAccept = checkParams(cookieBannerScript, "data-cookie-banner-text-accept", "Fermer");
        var cookieBannerTextDecline = checkParams(cookieBannerScript, "data-cookie-banner-text-decline", "Refuser");

        var cookieBannerElementId = checkParams(cookieBannerScript, "data-cookie-banner-element-id", "cookie-banner");
        var cookieBannerUrl = checkParams(cookieBannerScript, "data-cookie-banner-url", "http://www.abes.fr/");

        var cookieBannerDecline = checkParams(cookieBannerScript, "data-cookie-banner-decline", false);

        // Création de l'élément
        var cookieBannerDiv = document.createElement("div");
        cookieBannerDiv.innerHTML = "" +
            "<div id='" + cookieBannerElementId + "' style='" + cookieBannerStyle + "'>" +
            ((cookieBannerDecline) ? "<a style='" + cookieBannerStyleDecline + "' onclick='cookieBannerDecline()' href='#'>" + cookieBannerTextDecline + "</a>" : "") +
            "<a style='" + cookieBannerStyleAccept + "' onclick='cookieBannerAccept()' href='#'>" + cookieBannerTextAccept + "</a>" +
            "<span style='" + cookieBannerStyleText + "'>" +
            cookieBannerText +
            "&nbsp;&nbsp;<a style='" + cookieBannerStyleMore + "' href='" + cookieBannerUrl + "'>" + cookieBannerTextMore + "</a>" +
            "</span>" +
            "</div>";

        // Insertion de l'élément
        document.body.insertBefore(cookieBannerDiv.firstChild, document.body.firstChild);
    }
}

/**
 * Vérifie que pour l'ID donné, si l'on doit utiliser la valeur par défault on non
 * @param script
 * @param id
 * @param value
 * @returns
 */
function checkParams(script, id, value) {
    var find = false;
    var i = 0;
    while (!find && i < script.attributes.length) {
        find = (script.attributes[i].name === id ? script.attributes[i].value : false);
        i++;
    }

    if (find)
        return find;
    else
        return value;
}

//Définition de la fonction d'appel une fois le cookie accepté (cookie limité à 13 mois)
function cookieBannerAccept() {
    setCookie("cookie-banner-accept", "1", 13 * 30);
    document.body.removeChild(document.body.firstChild);
}

//Définition de la fonction d'appel une fois le cookie refusé (cookie limité à 13 mois)
function cookieBannerDecline() {
    setCookie("cookie-banner-decline", "1", 13 * 30);
    document.body.removeChild(document.body.firstChild);
}

/**
 * Functions for cookies
 */
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');

    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ')
            c = c.substring(1);

        if (c.indexOf(name) === 0)
            return c.substring(name.length, c.length);
    }

    return "";
}

/**
 * START
 */
document.onreadystatechange = function () {
    var state = document.readyState;
    if (state === 'complete') {
        launchCookieBannerScript();
    }
};
