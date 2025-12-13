<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.lang.NumberFormatException" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<%@page import="fr.abes.derives.web.UserBean" %>
<jsp:include flush="false" page="checkgranted.jsp"></jsp:include>
<%
    Long orderId = (Long) request.getAttribute(RequestHelper.ATTRIBUTE_ORDER_ID);
    if (orderId == null) {
        //direct call of .JSP, trying to find orderid in parameters
        String strOrderId = request.getParameter(RequestHelper.ATTRIBUTE_ORDER_ID);

        orderId = null;
        if (strOrderId == null || "".equals(strOrderId.trim())) {
            orderId = null;
        } else {
            try {
                orderId = Long.valueOf(strOrderId);
            } catch (NumberFormatException e) {
                System.err.print("silent NumberFormatException on orderId " + strOrderId + " : " + e.getMessage());
                ;
                orderId = null;
            }
        }
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dojo/resources/dojo.css"/>
    <link rel="stylesheet" type="text/css" href="dojo-release-1.5.0/dijit/themes/claro/claro.css"/>
    <script src="jquery-1.5.min.js"></script>
    <title>Exécution de Commande - SELF Sudoc - Service En Ligne de Fichiers Sudoc</title>
</head>
<body class="claro">
<jsp:include flush="false" page="menu.jsp"></jsp:include>
<div align="center"><font face="arial,helvetica">Progression de votre commande <%=orderId%> ...&nbsp;<img
        id="imgProgress" src="img/103.gif" width="60" height="9" alt="ProgressBar"/></font></div>
<br/>
<table id="orders" width="100%" border="0" cellpadding="0" cellspacing="0">
</table>

<script type="text/javascript"><!--
//<![CDATA[

function etatColor(etat) {
    if ('O' === etat) return '#66ff66';
    if ('W' === etat) return '#ffcc66';
    if ('X' === etat) return '#ff6600';
    return '#ffffcc';
}

function successCallBack(data, textStatus, XMLHttpRequest) {
    var finished = true;
    $('#orders').empty(); //emptied
    //headers
    $('<tr/>').html('<td width="13%" bgcolor="#cccccc"><font face="arial,helvetica" size="1"><b>ORDER_ID</b></font></td><td width="13%" bgcolor="#cccccc" align="center"><font face="arial,helvetica" size="1"><b>&nbsp;&nbsp;DATE</b></font></td><td width="13%" bgcolor="#cccccc" align="center"><font face="arial,helvetica" size="1"><b>&nbsp;&nbsp;ILN</b></font></td><td width="13%" bgcolor="#cccccc"><font face="arial,helvetica" size="1"><b>RCR</b></font></td><td width="13%" bgcolor="#cccccc"><b>%</b></td><td width="13%" bgcolor="#cccccc" align="center"><font face="arial,helvetica" size="1"><b>TELECHARGER</b></font></td><td width="13%" bgcolor="#cccccc"><font face="arial,helvetica" size="1"><b>&nbsp;&nbsp;#NOTICES</b></font></td><td width="9%" bgcolor="#cccccc"><font face="arial,helvetica" size="1"><b>&nbsp;&nbsp;ERREUR</b></font></td>').appendTo("#orders");
    $('<tr/>').html('<td colspan="7">&nbsp;</td>').appendTo("#orders");
    for (var i = 0; i < data.length; i++) {
        var exportid = data[i].exportid;
        var iln = data[i].iln;
        var rcr = data[i].rcr;
        var format = data[i].format;
        var dateextraction = data[i].dateextraction;
        var nbnotices = data[i].nbnotices;
        if (nbnotices === undefined) {
            nbnotices = '';
        }
        var exceptionmsg = data[i].exceptionmsg;
        if (exceptionmsg === undefined) {
            exceptionmsg = '';
        }
        var md5hashwithextension = data[i].md5hashwithextension;
        var filename = 'notices_' + rcr + '_' + exportid + '.' + format;

        var etat = '<font color="' + etatColor(data[i].etatextracted) + '">' + fullblock + '</font>';
        etat = etat + '<font color="' + etatColor(data[i].etatcleaned) + '">' + fullblock + '</font>';
        etat = etat + '<font color="' + etatColor(data[i].etatgrouped) + '">' + fullblock + '</font>';
        etat = etat + '<font color="' + etatColor(data[i].etatfiltered) + '">' + fullblock + '</font>';
        etat = etat + '<font color="' + etatColor(data[i].etatsorted) + '">' + fullblock + '</font>';
        etat = etat + '<font color="' + etatColor(data[i].etatxhtml) + '">' + fullblock + '</font>';
        if ('pdf' === format) {
            etat = etat + '<font color="' + etatColor(data[i].etatpdf) + '">' + fullblock + '</font>';
        }
        if ('rtf' === format) {
            etat = etat + '<font color="' + etatColor(data[i].etatrtf) + '">' + fullblock + '</font>';
        }
        if ('slk' === format) {
            etat = etat + '<font color="' + etatColor(data[i].etatslk) + '">' + fullblock + '</font>';
        }

        var lientelecharger = '';

        if (('pdf' === format && 'O' === data[i].etatpdf) || ('rtf' === format && 'O' === data[i].etatrtf) || ('slk' === format && 'O' === data[i].etatslk)) {
            lientelecharger = '<a href="<%=RequestHelper.DOWNLOAD_URI%>' + md5hashwithextension + '" target="_blank"><img border="0" src="img/' + format + '.gif" alt="' + format + '" width="16" height="16"/></a>';
        } else {
            finished = false;
        }

        $('<tr/>').html('<td valign="top"><font size="2">' + exportid + '</font></td><td  valign="top" align="center"><nobr><font size="2">' + dateextraction + '</font></nobr></td><td valign="top" align="center"><font size="2">' + iln + '</font></td><td valign="top"><font size="2">' + rcr + '</font></td><td valign="top">' + etat + '</td><td valign="top" align="center">' + lientelecharger + '</td><td valign="top"><font size="2">' + nbnotices + '</font></td><td valign="top"><font size="2" color="#ff0000">' + exceptionmsg + '</font></td>').appendTo("#orders");
    }

    if (finished) {
        var imgProgress = document.getElementById('imgProgress');
        imgProgress.src = 'img/blank.gif';
        imgProgress.alt = '';
        //TODO TMX : supprimer le texte "Progression ...", remplacer par "Terminé", stopper request Ajax
    }
}

function errorCallBack(XMLHttpRequest, textStatus, errorThrown) {
    alert(textStatus + ' ' + XMLHttpRequest.responseText);
}

function refreshOrders() {
    $.ajax({
        type: 'POST',
        url: 'ordersJSON',
        data: '<%if (orderId != null) {
				out.write(RequestHelper.ATTRIBUTE_ORDER_ID);
				out.write("=");
				out.write(orderId.toString());
			}%>',
        success: successCallBack,
        error: errorCallBack,
        dataType: 'json'
    });

    return true;
}

function timedCount() {
    refreshOrders();
    t = setTimeout("timedCount()", 3000);
}

var fullblock = '&#x2588;' //unicode for FULL BLOCK character

window.onload = timedCount;
//]]>
-->
</script>
</body>
</html>