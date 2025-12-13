<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<%@page import="fr.abes.derives.web.UserBean" %>
<jsp:include flush="false" page="checkgranted.jsp"></jsp:include>
<%
    UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
    String unicasHref = "http://www.sudoc.fr/services/unicas/" + user.getIln();
    String iln2rcrHref = "http://www.idref.fr/services/iln2rcr/" + user.getIln() + "&format=application/vnd.ms-excel";
    String iln2ppnHref = "http://www.sudoc.fr/services/generic/?servicekey=iln2ppn&iln=" + user.getIln() + "&format=application/vnd.ms-excel";
    String iln2adrHref = "http://www.sudoc.fr/services/generic/?servicekey=adressecat&iln=" + user.getIln() + "&format=application/vnd.ms-excel";
    String iln2usrHref = "http://www.sudoc.fr/services/generic/?servicekey=userscr&login=" + user.getUserKey() + "&password=&iln=" + user.getIln() + "&format=application/vnd.ms-excel";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">

    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dojo/resources/dojo.css"/>
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dijit/themes/claro/claro.css"/>

    <style type="text/css">
        .button {
            border-top: 1px solid #9abfd6;
            background: #336fb7;
            background: -webkit-gradient(linear, left top, left bottom, from(#18191a), to(#65a9d7));
            background: -webkit-linear-gradient(top, #18191a, #65a9d7);
            background: -moz-linear-gradient(top, #18191a, #65a9d7);
            background: -ms-linear-gradient(top, #18191a, #65a9d7);
            background: -o-linear-gradient(top, #18191a, #65a9d7);
            padding: 5.5px 11px;
            -webkit-border-radius: 8px;
            -moz-border-radius: 8px;
            border-radius: 8px;
            -webkit-box-shadow: rgba(0, 0, 0, 1) 0 1px 0;
            -moz-box-shadow: rgba(0, 0, 0, 1) 0 1px 0;
            box-shadow: rgba(0, 0, 0, 1) 0 1px 0;
            text-shadow: rgba(0, 0, 0, .4) 0 1px 0;
            color: white;
            font-size: 15px;
            font-family: Helvetica, Arial, Sans-Serif;
            text-decoration: none;
            vertical-align: middle;
        }

        .button:hover {
            border-top-color: #19384d;
            background: #19384d;
            color: #ccc;
        }

        .button:active {
            border-top-color: #1b435e;
            background: #1b435e;
        }
    </style>


    <title>Autres Services - SELF Sudoc - Service En Ligne de Fichiers Sudoc</title>
</head>

<body class="claro">
<jsp:include flush="false" page="menu.jsp"></jsp:include>
<center>
    <font size="2" face="arial,helvetica" color="#b73734">
        <b>Un clic suffit pour lancer l'extraction dans une nouvelle fenêtre (ou onglet) du navigateur. Le processus
            peut être long. Merci de patienter...</b>
    </font><br/><br/>
    <table cellspacing="0" cellpadding="0" border="1" width="80%">
        <tr>
            <td witdh="10%">&nbsp;</td>
            <td align="left" width="90%"><font size="5" face="arial,helvetica">DESCRIPTION DU SERVICE</font></td>
        </tr>
        <tr>
            <td witdh="10%"><a class="button" href="<%=unicasHref %>" target="_blank">
                <b>UNICA</b>
            </a></td>
            <td align="left" width="90%"><font size="3" face="arial,helvetica"><br/><br/>
                <b>&nbsp;Les localisations uniques de l'ILN (ou UNICA)&nbsp;:</b> obtenir la correspondance PPN,RCR au
                format tabulé (csv)<br/><br/>
            </font></td>
        </tr>
        <tr>
            <td witdh="10%"><a class="button" href="<%=iln2ppnHref %>" target="_blank">
                <b>ILN2PPN</b>
            </a></td>
            <td align="left" width="90%"><font size="3" face="arial,helvetica"><br/><br/>
                <b>&nbsp;Pour son ILN&nbsp</b>, obtenir la liste de ses PPN (format csv)<br/><br/>
            </font></td>
        </tr>
        <tr>
            <td witdh="10%"><a class="button" href="<%=iln2rcrHref %>" target="_blank">
                <b>ILN2RCR</b>
            </a></td>
            <td align="left" width="90%"><font size="3" face="arial,helvetica"><br/><br/>
                <b>&nbsp;Pour son ILN&nbsp</b>, obtenir la liste de ses RCR (format csv)<br/><br/>
            </font></td>
        </tr>
        <% if (RequestHelper.isGranted(user)) {
            out.print("<tr><td width=\"10%\"><a class=\"button\" href=\"");
            out.print(iln2usrHref);
            out.print("\" target=\"_blank\"><b>ILN2USR</b></a></td><td align=\"left\" width=\"90%\"><font size=\"3\" face=\"arial,helvetica\"><br/><br/><b>&nbsp;Pour son ILN&nbsp</b>, obtenir la liste de ses utilisateurs par RCR (format csv)<br/><br/></font></td></tr>");
        } %>
        <tr>
            <td witdh="10%"><a class="button" href="<%=iln2adrHref %>" target="_blank">
                <b>ILN2ADR</b>
            </a></td>
            <td align="left" width="90%"><font size="3" face="arial,helvetica"><br/><br/>
                <b>&nbsp;Pour son ILN&nbsp</b>, obtenir la liste des adresses mail de signalement de doublons par RCR
                (format csv)<br/><br/>
            </font></td>
        </tr>
    </table>
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