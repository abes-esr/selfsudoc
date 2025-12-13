<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.util.Random" %>
<%@page import="fr.abes.derives.web.UserBean" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<jsp:include flush="false" page="checkgranted.jsp"></jsp:include>
<%
    Random randomGenerator = new Random();
    int sharedSecret = randomGenerator.nextInt(Integer.MAX_VALUE);
    session.setAttribute(RequestHelper.KEY_SHAREDSECRET, sharedSecret);

    UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
    Boolean acceptedTerms = (Boolean) session.getAttribute(RequestHelper.KEY_ACCEPTTERMS);
    if (acceptedTerms == null) {
        //fix NPE
        acceptedTerms = false;
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">

    <script type="text/javascript">
        //var djConfig = {isDebug: true,"bindEncoding": "utf8", usePlainJson: true, popup:true,parseOnLoad:false};
    </script>
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dojo/resources/dojo.css"/>
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dijit/themes/claro/claro.css"/>
    <script src="dojo-release-1.5.0/dojo/dojo.js" type="text/javascript"></script>

    <script type="text/javascript"><!--
    //<![CDATA[

    dojo.require("dijit.form.Button");
    dojo.require("dijit.Dialog");
    dojo.require("dijit.form.RadioButton");

    function accept() {
        dijit.byId("conventionDialog").hide();
        var acceptURL = 'acceptterms?s=<%=sharedSecret%>';
        window.location = acceptURL;
    }

    function decline(isCentreRegional) {
        dijit.byId("conventionDialog").hide();
        if (!isCentreRegional) {
            window.location = 'logout';
        } else {
            window.location = 'http://www.abes.fr/Sudoc/Services-disponibles/Exports-a-la-demande';
        }
    }

    var conventionDialog;

    dojo.addOnLoad(function () {
        if (!<%=acceptedTerms%>) {
            if (<%=RequestHelper.isCentreRegional(user)%>) {
                // create the Centre Regional version dialog
                conventionDialog = new dijit.Dialog({
                    id: 'conventionDialog',
                    title: '<font color="#ff0000">AVERTISSEMENT</font>',
                    href: 'convcentreregional.html',
                    execute: 'alert(\'submitted w/args:\n\' + dojo.toJson(arguments[0], true));',
                    style: ''
                });
            }

            if (<%=RequestHelper.isCoordinateur(user)%>) {
                // create the Coordinateur version dialog
                conventionDialog = new dijit.Dialog({
                    id: 'conventionDialog',
                    title: '<font color="#ff0000">AVERTISSEMENT</font>',
                    href: 'convcoordinateur.html',
                    execute: 'alert(\'submitted w/args:\n\' + dojo.toJson(arguments[0], true));',
                    style: ''
                });
            }

            dojo.connect(conventionDialog, 'onCancel', function (e) {
                // this will be run anytime you or someone calls dialog.hide()
                window.location = 'logout';
            });

            conventionDialog.show();
        }
    });
    //]]>
    --></script>
    <!-- Cookies CNIL -->
    <script src="js/bandeau.js" type="text/javascript" id="cookie-banner-script"></script>

    <title>Votre Profil - SELF Sudoc - Service En Ligne de Fichiers Sudoc</title>
</head>
<body class="claro">
<jsp:include flush="false" page="menu.jsp"></jsp:include>

<div align="center"><br></br>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="20%">&nbsp;</td>
            <td with="40%" valign="top" align="left"><b>Vos informations</b><br/><%=user%>
            </td>
            <td with="40%" valign="middle" align="left">
                <% if (acceptedTerms) { %>
                <div><font color="#00cc00" size="3">Vous avez accepté les conditions d'utilisation.</font></div>
                <% } else { %>
                <div><font color="#ff0000" size="3">Vous n'avez pas accepté les conditions d'utilisation !</font></div>
                <% } %>
            </td>
        </tr>
    </table>
</div>

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