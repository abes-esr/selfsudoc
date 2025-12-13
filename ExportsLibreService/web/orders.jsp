<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<%@page import="fr.abes.derives.web.UserBean" %>
<jsp:include flush="false" page="checkgranted.jsp"></jsp:include>
<%
    UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dojo/resources/dojo.css"/>
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dijit/themes/claro/claro.css"/>
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dojox/grid/resources/Grid.css"/>
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dojox/grid/resources/claroGrid.css"/>
    <style type="text/css">
        .dojoxGrid table {
            margin: 0;
        }

        html, body {
            width: 100%;
            height: 100%;
            margin: 0;
        }

        .input_text_iln {
            background-color: white;
            width: 20px;
            height: 14px;
            color: black;
            font-size: 10px;
        }

        .input_text_orderid {
            background-color: white;
            width: 80px;
            height: 14px;
            color: black;
            font-size: 10px;
        }
    </style>
    <script src="dojo-release-1.5.0/dojo/dojo.js" type="text/javascript"></script>

    <script type="text/javascript"><!--
    //<![CDATA[
    dojo.require("dojox.grid.DataGrid");
    dojo.require("dojox.data.QueryReadStore");
    dojo.require("dojox.html.entities");

    function noSort(index) {
        // Argument:  1 base column index number
        if (index == 2 || index == -2) {  // enable sorting asc,desc on DATE colmun
            return true;
        }
        return false;
    }

    function htmlAnchor(rowIndex, item) {
        if (item != null) {
            var object = item.i;
            var md5hashwithextension = object.md5hashwithextension;
            var format = object.format;

            var lientelecharger = '';

            if (('pdf' === format && 'O' === object.etatpdf) || ('rtf' === format && 'O' === object.etatrtf) || ('slk' === format && 'O' === object.etatslk)) {
                lientelecharger = '<a href="<%= RequestHelper.DOWNLOAD_URI %>' + md5hashwithextension + '" target="_blank"><img border="0" src="img/' + format + '.gif" alt="' + format + '" width="16" height="16"/></a>';
            }
            return lientelecharger;
        }
    }

    function formatDownload(input) {
        return dojox.html.entities.decode(input)
    }

    function formatUndefinedBlank(input) {
        if (input === undefined) {
            return '';
        }
        return input;
    }

    function formatException(input) {
        if (input === undefined) {
            return '';
        }
        return '<font size="2" color="#ff0000">' + input + '</font>';
    }

    function resetORDERID() {
        var filtreORDERID = document.getElementById('filtreORDERID');
        filtreORDERID.value = '';
        var imgFiltreORDERID = document.getElementById('imgFiltreORDERID');
        imgFiltreORDERID.src = 'img/blank.gif';
        imgFiltreORDERID.alt = '';
    }

    function resetILN() {
        var filtreILN = document.getElementById('filtreILN');
        if (filtreILN != undefined) {
            filtreILN.value = '';
            var imgFiltreILN = document.getElementById('imgFiltreILN');
            imgFiltreILN.src = 'img/blank.gif';
            imgFiltreILN.alt = '';
        }
    }

    function filtrageILN(ilnValue) {
        if ('' == dojo.trim(ilnValue)) {
            noFiltre();
        } else {
            var theWidget = dijit.byId('theGrid');
            var serverSideQuery = {name: '*', iln: ilnValue};
            theWidget.filter(serverSideQuery, true); //will change query and rerender

            var imgFiltreILN = document.getElementById('imgFiltreILN');
            imgFiltreILN.src = 'img/accept.png';
            imgFiltreILN.alt = 'ON';

            //reset other input text
            resetORDERID();
        }
    }

    function filtrageORDERID(orderIdValue) {
        if ('' == dojo.trim(orderIdValue)) {
            noFiltre();
        } else {
            var theWidget = dijit.byId('theGrid');
            var serverSideQuery = {name: '*', orderid: orderIdValue};
            theWidget.filter(serverSideQuery, true); //will change query and rerender

            var imgFiltreORDERID = document.getElementById('imgFiltreORDERID');
            imgFiltreORDERID.src = 'img/accept.png';
            imgFiltreORDERID.alt = 'ON';

            //reset other input text
            resetILN();
        }
    }

    function noFiltre() {
        var theWidget = dijit.byId('theGrid');
        var serverSideQuery = {name: '*'}; //all items from the store
        theWidget.filter(serverSideQuery, true); //will change query and rerender

        //reset all inputs text
        resetILN();
        resetORDERID();
    }

    dojo.addOnLoad(function () {
        var store = new dojox.data.QueryReadStore({url: 'ordersJSON'});

        // set the layout structure:
        var layout = [
            {name: 'ORDER_ID', field: 'exportid', width: '100px'},
            {name: 'DATE', field: 'dateextraction', width: '150px'},
            {name: 'ILN', field: 'iln'},
            {name: 'RCR', field: 'rcr'},
            {name: '#NOTICES', field: 'nbnotices'},
            {name: 'LIEN', field: 'md5hashwithextension', get: htmlAnchor, formatter: formatDownload},
            {name: 'TAILLE', field: 'byteslength', formatter: formatUndefinedBlank},
            {name: 'ERREUR', field: 'exceptionmsg', width: '500px', formatter: formatException}
        ];

        var query = {name: '*'}; //all items from the store

        var grid = new dojox.grid.DataGrid({
            id: 'theGrid',
            autoHeight: 30,
            rowsPerPage: 45,
            autoWidth: true,
            canSort: noSort,
            structure: layout,
            query: query,
            store: store,
            sortInfo: -2
        }, dojo.byId('gridContainer'));

        grid.startup();
    });

    //]]>
    -->
    </script>
    <title>Historique Commandes - SELF Sudoc - Service En Ligne de Fichiers Sudoc</title>
</head>
<body class="claro">
<jsp:include flush="false" page="menu.jsp"></jsp:include>
<table border="0" cellspacing="0" cellpadding="0" width="1100">
    <tr>
        <td width="20%">
            <nobr>&nbsp;<img id="imgFiltreORDERID" border="0" src="img/blank.gif" alt="" width="28" height="25"/>ORDER_ID&nbsp;:&nbsp;<input
                    type="text" value="" maxlength="13" size="13" class="input_text_orderid" name="filtreORDERID"
                    id="filtreORDERID">&nbsp;<a href="#"
                                                onclick="javascript:filtrageORDERID(document.getElementById('filtreORDERID').value);"><img
                    border="0" src="img/icon_search.gif" alt="?" width="16" height="16"/></a></nobr>
        </td>
        <td width="20%">
            <%
                if (user.isAdmin()) {
            %>
            <nobr>&nbsp;<img id="imgFiltreILN" border="0" src="img/blank.gif" alt="" width="28" height="25"/>ILN&nbsp;:&nbsp;<input
                    type="text" value="" maxlength="3" size="3" class="input_text_iln" name="filtreILN" id="filtreILN">&nbsp;<a
                    href="#" onclick="javascript:filtrageILN(document.getElementById('filtreILN').value);"><img
                    border="0" src="img/icon_search.gif" alt="?" width="16" height="16"/></a></nobr>
            <%
                }
            %>
        </td>
        <td width="50%">&nbsp;</td>
        <td width="10%"><a style="text-decoration:none;color: #000000;" href="#" onclick="javascript:noFiltre();"><img
                border="0" src="img/button_cancel_icon22.gif" alt="(x)" width="22" height="22"/>&nbsp;Filtres</a></td>
    </tr>
</table>
<br/>

<div id="gridContainer" style="width: 100%; height: 100%;"></div>
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