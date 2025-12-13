<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<%
    String localHostName = (String) application.getAttribute(RequestHelper.KEY_PROPS_LOCALHOSTNAME);
    if (localHostName == null) {
        localHostName = "";
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">
    <link rel="icon" href="img/favicon.ico" type="image/ico"/>
    <link rel="shortcut icon" href="img/favicon.ico" type="image/ico"/>
    <script type="text/javascript"><!--
    //<![CDATA[
    function focusing() {
        var login = document.getElementById('login');
        login.focus();
    }

    window.onload = focusing;
    //]]>
    --></script>
    <link rel="stylesheet" type="text/css" media="screen" href="demos.css">
    <style type="text/css">
        .input_text {
            background-color: #bebebe;
            width: 100px;
            height: 18px;
            color: black;
            font-size: 14px;
        }
    </style>
    <title></title><!-- no title for low profile -->
</head>
<body>
<!-- ABES : <% out.write(localHostName); %> -->
<center>
    <form method="POST" action="j_security_check">
        <table with="100%" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" align="center"><img src="img/logo_abes_small.gif" width="236" height="77" alt=""/></td>
            </tr>
            <tr>
                <td td colspan="2">
                    <hr size="1">
                    &nbsp;
                    </hr>
                </td>
            </tr>
            <tr>
                <td width="50%" align="left"><font face="arial,helvetica">Login&nbsp;: </font></td>
                <td width="50%" align="left"><input class="input_text" type="text" name="j_username" id="login"
                                                    size="16" maxlength="64"></td>
            </tr>
            <tr>
                <td align="left"><font face="arial,helvetica">Mot de passe&nbsp;:</font></td>
                <td align="left"><input class="input_text" type="password" name="j_password" size="16" maxlength="16">
                </td>
            </tr>
            <tr>
                <td td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td td colspan="2" align="center"><input type="submit" value="Entrer">&nbsp;<input
                        type="reset" value="Annuler"></td>
            </tr>
        </table>
    </form>
    <div id="dt_example">&nbsp;<br/>
        <nobr><a href="https://abes.fr/pages-cgu/conditions-generales-utilisation-sites-abes.html" target="_blank">Conditions
            G&eacute;n&eacute;rales d'Utilisation (CGU)</a>&nbsp;<a
                href="https://abes.fr/pages-mentions-legales/self.sudoc.html" target="_blank">Mentions
            L&eacute;gales</a>&nbsp;<a href="https://abes.fr/pages-accessibilite/self.sudoc.html" target="_blank">Accessibilit&eacute;</a>&nbsp;<a
                href="https://abes.fr/pages-donnees-personnelles/self.sudoc.html" target="_blank">Donn&eacute;es
            Personnelles</a></nobr>
    </div>
</center>

</body>
<!-- Matomo -->
<script type="text/javascript">
    var _paq = window._paq || [];
    /* tracker methods like "setCustomDimension" should be called before "trackPageView" */
    _paq.push(['trackPageView']);
    _paq.push(['enableLinkTracking']);
    (function () {
        var u = "//piwik.abes.fr/";
        _paq.push(['setTrackerUrl', u + 'piwik.php']);
        _paq.push(['setSiteId', '8']);
        var d = document, g = d.createElement('script'), s = d.getElementsByTagName('script')[0];
        g.type = 'text/javascript';
        g.async = true;
        g.defer = true;
        g.src = u + 'piwik.js';
        s.parentNode.insertBefore(g, s);
    })();
</script>
<!-- End Matomo Code -->
</html>