<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page import="fr.abes.derives.web.UserBean" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.Set" %>
<%@page import="fr.abes.derives.web.ListRcrJSONCommand" %>


<jsp:include flush="false" page="checkgranted.jsp"></jsp:include>
<%
    UserBean user = (UserBean) session
            .getAttribute(UserBean.KEY_PROFILE);

    Map<String, Map<String, String>> layouts = (Map<String, Map<String, String>>) application.getAttribute(RequestHelper.KEY_MAP_LAYOUTS);
    Map<String, String> pivotLayouts = layouts.get(RequestHelper.PDFRTF);
    Map<String, String> sylkLayouts = layouts.get(RequestHelper.SYLK);

    Map<String, String> exclusions = (Map<String, String>) application.getAttribute(RequestHelper.KEY_MAP_EXCLUSIONS);
    Set<String> exclusionsKeys = exclusions.keySet();
    int sizeExclusions = exclusionsKeys.size();


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

    <style type="text/css">

        .input_text {
            background-color: white;
            width: 50px;
            height: 14px;
            color: black;
            font-size: 10px;
        }

    </style>
    <script src="dojo-release-1.5.0/dojo/dojo.js" type="text/javascript"></script>

    <script type="text/javascript"><!--
    //<![CDATA[
    dojo.require("dijit.Dialog");
    dojo.require("dijit.form.Button");

    function blockUnblockSelect(id, val) {
        var toBlock = document.getElementById(id);
        toBlock.disabled = !val;
    }

    function addslashes(ch) {
        ch = ch.replace(/\\/g, "\\\\");
        ch = ch.replace(/\'/g, "\\'");
        ch = ch.replace(/\"/g, "\\\"");
        return ch;
    }

    function clearBasket() {
        dojo.empty('basket'); //emptied
        var basketTBODY = dojo.create('tbody', null, basket);
        dojo.attr(basketTBODY, {id: 'basketTBODY'});
        var tr = dojo.create('tr', null, basketTBODY);
        dojo.attr(tr, {height: '40', bgcolor: '#cccccc'});
        var td1 = dojo.create('td', {innerHTML: '&nbsp;<nobr><img src="img/ILN.png" width="22" height="22" align="bottom" alt="RCR"/>&nbsp;<font face="arial,helvetica" size="1"><b>RCR</b></font></nobr>'}, tr);
        dojo.attr(td1, {width: '23%', align: 'left', valign: 'middle', bgColor: '#cccccc'});
        var td2 = dojo.create('td', {innerHTML: '<nobr><img src="img/NbreNotices.png" width="22" height="22" align="bottom" alt="NB NOTICES"/>&nbsp;<font face="arial,helvetica" size="1"><b>NB NOTICES APPROX.</b></font></nobr>'}, tr);
        dojo.attr(td2, {width: '23%', align: 'left', valign: 'middle', bgColor: '#cccccc'});
        var td3 = dojo.create('td', {innerHTML: '<nobr><img src="img/Telecharger.png" width="22" height="22" align="bottom" alt="SORTIE"/>&nbsp;<font face="arial,helvetica" size="1"><b>TYPE DE SORTIE</b></font></nobr>'}, tr);
        dojo.attr(td3, {width: '23%', align: 'left', valign: 'middle', bgColor: '#cccccc'});
        var td4 = dojo.create('td', {innerHTML: '<nobr><img src="img/cle12.png" width="22" height="22" align="bottom" alt="EXCLURE"/>&nbsp;<font face="arial,helvetica" size="1"><b>EXCLURE DES ZONES</b></font><img src="img/pdf.gif" width="16" height="16"/><img src="img/rtf.gif" width="16" height="16"/></nobr>'}, tr);
        dojo.attr(td4, {width: '23%', align: 'center', valign: 'middle', bgColor: '#cccccc'});
        var td5 = dojo.create('td', {innerHTML: '<nobr><img src="img/trash.png" width="24" height="24" align="bottom" alt="SUPPRIMER"/>&nbsp;<font face="arial,helvetica" size="1"><b>SUPPRIMER</b></font></nobr>'}, tr);
        dojo.attr(td5, {width: '8%', align: 'left', valign: 'middle', bgColor: '#cccccc'});
    }

    function deleteFromBasket(library) {
        //first delete hidden linked exclude fields !
        for (var i in arrayExclNames) {
            var fieldName = arrayExclNames[i][0];
            var hiddenId = 'hidden_' + library + '_' + fieldName;
            var toRemove = document.getElementById(hiddenId);
            dojo.destroy(hiddenId);
        }
        //effective delete from basket
        dojo.destroy('basket_' + library);
        dojo.destroy('separation_' + library);
    }

    function checkLimitsOfLibrary(library) {
        var idlow = '<%=RequestHelper.PARAMETER_PREFIX_LOW %>' + '.' + library;
        var idhigh = '<%=RequestHelper.PARAMETER_PREFIX_HIGH %>' + '.' + library;

        var low = document.getElementById(idlow);
        var high = document.getElementById(idhigh);

        if (low == null && high == null) {
            //not limited, always true
            return true;
        }

        //limited -> we check values

        var limitlow = low.value;
        var limithigh = high.value;

        if (isNaN(limitlow) || isNaN(limithigh)) {
            alert('Limites (RCR ' + library + ') : saisie incorrecte !');
            low.value =<%= RequestHelper.DEFAULT_LOW_LIMIT %>;
            high.value =<%= RequestHelper.DEFAULT_HIGH_LIMIT %>;
            return false;
        } else {
            var size = (limithigh - limitlow);
            if (size <= 0) {
                alert('Limites (RCR ' + library + ') : aucune notice retenue !');
                low.value =<%= RequestHelper.DEFAULT_LOW_LIMIT %>;
                high.value =<%= RequestHelper.DEFAULT_HIGH_LIMIT %>;
                return false;
            } else {
                if (size ><%=RequestHelper.MAX_SIZE %>) {
                    alert('Limites (RCR ' + library + ') : ' + <%=RequestHelper.MAX_SIZE %> +' notices maximum !');
                    low.value =<%= RequestHelper.DEFAULT_LOW_LIMIT %>;
                    high.value =<%= RequestHelper.DEFAULT_HIGH_LIMIT %>;
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    function checkLimits() {
        var nbRows = basket.rows.length;
        for (var r = 1; r < nbRows; r++) { //first row is legend, not a library, skip it

            var tr = basket.rows[r];
            var fullid = tr.id;
            var splitArray = fullid.split('basket_');
            var library = splitArray[1];

            if (library === undefined) {
            } else {
                if (!checkLimitsOfLibrary(library)) {
                    return false;
                }
            }
        }
        return true;
    }

    function htmlCounted(library, counted, isLimited) {
        //console.debug('isLimited',isLimited);
        var html = '';
        if (isLimited) {
            html = html + '<nobr><font size="1"><font color="#ff0000">' + counted + '&nbsp;!&nbsp;</font>';
            html = html + 'entre&nbsp;';
            html = html + '<input type="text" id="<%=RequestHelper.PARAMETER_PREFIX_LOW %>' + '.' + library + '" name="<%=RequestHelper.PARAMETER_PREFIX_LOW %>' + '.' + library + '" class="input_text" size="6" maxlength="6" value="<%= RequestHelper.DEFAULT_LOW_LIMIT %>" onblur="javascript:checkLimitsOfLibrary(\'' + library + '\');" />';
            html = html + '&nbsp;et&nbsp;';
            html = html + '<input type="text" id="<%=RequestHelper.PARAMETER_PREFIX_HIGH %>' + '.' + library + '" name="<%=RequestHelper.PARAMETER_PREFIX_HIGH %>' + '.' + library + '" class="input_text" size="6" maxlength="6" value="<%= RequestHelper.DEFAULT_HIGH_LIMIT %>" onblur="javascript:checkLimitsOfLibrary(\'' + library + '\');" />';
            html = html + '</font></nobr>';
        } else {
            html = html + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + counted;
        }

        return html;
    }

    function updateFieldsToExclude(library) {
        //for each checked input in pop-up add a hidden input to global form
        var theSpanFields = document.getElementById('excludedFields');

        for (var i in arrayExclNames) {
            var fieldName = arrayExclNames[i][0];
            var excludedPopUpid = '<%=RequestHelper.PARAMETER_EXCLUDED %>' + '_popup_' + fieldName;
            var hiddenId = 'hidden_' + library + '_' + fieldName;
            var inputPopUp = document.getElementById(excludedPopUpid);

            if (inputPopUp.checked) {
                //test if already exists !
                var existAlready = document.getElementById(hiddenId);
                if (existAlready == null) {
                    var hiddenInput = dojo.create('input', {type: 'hidden'}, theSpanFields);
                    dojo.attr(hiddenInput, {
                        name: '<%=RequestHelper.PARAMETER_EXCLUDED %>',
                        value: '' + library + '.' + fieldName,
                        id: '' + hiddenId
                    });
                }
            } else {
                var toRemove = document.getElementById(hiddenId);
                dojo.destroy(hiddenId);
            }
        }
        return true;
    }

    function showExcludables(library) {
        var title = '<nobr>Zones à exclure</nobr>';
        var content = '<b>RCR&nbsp;' + library + '</b>';
        content = content + '<div><br/>';

        for (var i in arrayExclNames) {
            var fieldName = arrayExclNames[i][0];
            var urlGuide = arrayExclNames[i][1];
            var hiddenId = 'hidden_' + library + '_' + fieldName;
            //test to precheck already existing
            var existAlready = document.getElementById(hiddenId);

            content = content + '<input type=\"checkbox\" id=\"<%=RequestHelper.PARAMETER_EXCLUDED %>_popup_' + fieldName + '\" name=\"<%=RequestHelper.PARAMETER_EXCLUDED %>\" value=\"' + library + '.' + fieldName + '\"';
            if (existAlready != null) {
                content = content + ' checked=\"checked\"';
            }
            content = content + '>&nbsp;&nbsp;<font size=\"2\"><a href=\"' + urlGuide + '\" target=\"_blank\">' + fieldName + '</a></font></input><br/>';
        }

        content = content + '</div>';
        content = content + '<center><button dojoType=\"dijit.form.Button\" type=\"submit\" onClick=\"javascript:return updateFieldsToExclude(' + library + ');\">OK</button></center>';

        excludeDataFieldDlg.set('title', title);
        excludeDataFieldDlg.set('content', content);
        excludeDataFieldDlg.show();
    }


    function addToBasket(library, short_name, counted, mustBeLimited) {

        var deleteButton = '<a href="#" onclick="javascript:deleteFromBasket(\'' + library + '\');"><img border="0" src="img/delete.png" alt="(x)" width="22" height="22"/></a>';
        var selectPivotId = 'select_pivot_' + library;
        var pivotLayouts = '<select style="width:12em" id="' + selectPivotId + '" name="<%=RequestHelper.PARAMETER_PIVOT_LAYOUT%>" disabled="true">';
        <%
            Set<String> xslStyleSheets = pivotLayouts.keySet();
            for (String styleSheet : xslStyleSheets) {
                String label = pivotLayouts.get(styleSheet);
                out.println("pivotLayouts = pivotLayouts + '<option value=\""+styleSheet+"'+'.'+library+'\">"+label+"</option>';");
            }
        %>
        pivotLayouts = pivotLayouts + '</select>';

        var selectSylkId = 'select_sylk_' + library;
        var sylkLayouts = '<select style="width:12em" id="' + selectSylkId + '" name="<%=RequestHelper.PARAMETER_SYLK_LAYOUT%>" disabled="true">';
        <%
            xslStyleSheets = sylkLayouts.keySet();
            for (String styleSheet : xslStyleSheets) {
                String label = sylkLayouts.get(styleSheet);
                out.println("sylkLayouts = sylkLayouts + '<option value=\""+styleSheet+"'+'.'+library+'\">"+label+"</option>';");
            }
        %>
        sylkLayouts = sylkLayouts + '</select>';

        var idInputPDF = '' + library + '.pdf';
        var idInputRTF = '' + library + '.rtf';
        var inputPDF = '<input type="checkbox" id="' + idInputPDF + '" name="<%=RequestHelper.PARAMETER_RCRFORMATS%>" value="' + library + '.pdf" onClick="javascript:blockUnblockSelect(\'' + selectPivotId + '\',this.checked||document.getElementById(\'' + idInputRTF + '\').checked);"><img border="0" src="img/pdf.gif" alt="pdf" width="16" height="16"/></input>';
        var inputRTF = '<input type="checkbox" id="' + idInputRTF + '" name="<%=RequestHelper.PARAMETER_RCRFORMATS%>" value="' + library + '.rtf" onClick="javascript:blockUnblockSelect(\'' + selectPivotId + '\',this.checked||document.getElementById(\'' + idInputPDF + '\').checked);"><img border="0" src="img/rtf.gif" alt="rtf" width="16" height="16"/></input>';
        var inputSYLK = '<input type="checkbox" name="<%=RequestHelper.PARAMETER_RCRFORMATS%>" value="' + library + '.slk" onClick="javascript:blockUnblockSelect(\'' + selectSylkId + '\',this.checked);"><img border="0" src="img/slk.gif" alt="slk" width="16" height="16"/></input>';
        var format = '<table width="100%" cellspacing="0" cellpadding="0" border="0"><tr><td valign="top" width="10%"><nobr>' + inputPDF + '</nobr><br/><nobr>' + inputRTF + '</nobr></td><td width="90%" valign="center">&gt;' + pivotLayouts + '</td></tr><tr><td valign="top"><nobr>' + inputSYLK + '</nobr></td><td valign="top">-' + sylkLayouts + '</td></tr></table>';

        //test if not already in basket
        var already = document.getElementById('basket_' + library);
        //alert('already='+already);
        //class is for .remove() and id is for testing
        if (already == null) {
            var basketTBODY = document.getElementById('basketTBODY');
            var tr = dojo.create('tr', null, basketTBODY);
            dojo.attr(tr, {
                id: 'basket_' + library
            });
            var td1 = dojo.create('td', {innerHTML: '<font size="2"><b>' + short_name + '&nbsp;(RCR:' + library + ')</b></font>'}, tr);
            dojo.attr(td1, {
                valign: 'top',
                align: 'left'
            });
            var td2 = dojo.create('td', {innerHTML: htmlCounted(library, counted, mustBeLimited)}, tr);
            dojo.attr(td2, {
                valign: 'top',
                align: 'left'
            });
            var td3 = dojo.create('td', {innerHTML: format}, tr);
            dojo.attr(td3, {
                valign: 'top',
                align: 'left'
            });
            var td4 = dojo.create('td', {innerHTML: '<font face=\"arial,helvetica\"><a href=\"#\" onclick=\"javascript:showExcludables(' + library + ');\"><img border=\"0\" src=\"img/edit24.png\" alt=\"Editer\" width=\"24\" height=\"24\"/></a></font>'}, tr);
            dojo.attr(td4, {
                valign: 'top',
                align: 'center'
            });
            var td5 = dojo.create('td', {innerHTML: deleteButton}, tr);
            dojo.attr(td5, {
                valign: 'top',
                align: 'left'
            });

            var tr2 = dojo.create('tr', null, basketTBODY);
            dojo.attr(tr2, {
                id: 'separation_' + library
            });
            var td6 = dojo.create('td', {innerHTML: '<hr size="1" width="100%" color="#cccccc">&nbsp;</hr>'}, tr2);
            dojo.attr(td6, {colSpan: '5'});
        } else {
            alert(short_name + ' (rcr: ' + library + ') déja dans votre panier !');
        }
    }

    function successCallBack(data, ioargs) {
        dojo.empty('libraries'); //emptied
        var tbody = dojo.create('tbody', null, libraries);
        for (var i = 0; i < data.length; i++) {
            var short_name = data[i].short_name;
            var library = data[i].library;
            var counted = data[i].counted;
            var mustBeLimited = data[i].mustBeLimited;
            if (counted === undefined) {
                counted = 0;
            }
            if (mustBeLimited === undefined) {
                mustBeLimited = false;
            }
            var tr = dojo.create('tr', null, tbody);
            var td1 = dojo.create('td', null, tr);
            dojo.create('td', {innerHTML: '&nbsp;<a style="text-decoration:none;color: #000000;" href="#" onclick="javascript:addToBasket(\'' + library + '\',\'' + addslashes(short_name) + '\',\'' + counted + '\',' + mustBeLimited + ');"><img border="0" src="img/add-icon.png" alt="(+)" width="22" height="22"/>&nbsp;' + short_name + '&nbsp;(RCR:' + library + ')</a>'}, tr);
        }
        secondDlg.show();
    }

    function errorCallBack(error, ioargs) {
        var message = '';
        switch (ioargs.xhr.status) {
            case 404:
                message = 'The requested page was not found';
                break;
            case 500:
                message = 'The server reported an error.';
                break;
            case 407:
                message = 'You need to authenticate with a proxy.';
                break;
            default:
                message = 'Unknown error.';
        }
    }

    function refreshRCR() {
        var iln = <%=user.getIln()%>;
        var xhrArgs = {
            encoding: 'UTF-8',
            handleAs: 'json-comment-optional',
            url: 'listlibrariesJSON',
            preventCache: true,
            content: {iln: iln},
            load: successCallBack,
            error: errorCallBack
        };
        var deferred = dojo.xhrGet(xhrArgs);

        return true;
    }

    var secondDlg;
    var basket;
    var libraries;
    var excludeDataFieldDlg;

    //copy Java variable content into javascript variable
    var arrayExclNames = [<% int i=0; for (String id : exclusionsKeys) {String url = exclusions.get(id); String pair="['"+id+"','"+url+"']"; out.write(pair); if (i<sizeExclusions-1) {out.write(", ");} i++; }%>];

    dojo.addOnLoad(function () {
        // create the excludedDataField dialog
        excludeDataFieldDlg = new dijit.Dialog({
            id: 'excludeDataFieldDlg',
            title: '',
            content: '',
            style: 'width:300px;'
        });

        // create the dialog:
        secondDlg = new dijit.Dialog({title: "Bibliothèques"});
        var html = '<table id="libraries" width="100%" border="0" cellpadding="0" cellspacing="1"></table>';
        secondDlg.set('content', html);
        basket = dojo.byId('basket');
        libraries = dojo.byId('libraries');

        clearBasket();
    });
    //]]>
    --></script>

    <title>Nouvel Export - SELF Sudoc - Service En Ligne de Fichiers Sudoc</title>
</head>
<body class="claro">
<jsp:include flush="false" page="menu.jsp"></jsp:include>
<form action="add" method="post" onsubmit="javascript:checkLimits();">
    <center>
        <table width="98%" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td colspan="2" align="left">
                    <nobr><font face="arial,helvetica" size="3"><b>Inclure&nbsp;les&nbsp;collections&nbsp;&nbsp;</b>
                        <input type="radio" name="<%=RequestHelper.PARAMETER_WITHCOLLECTIONS%>" value="true"
                               checked="checked"/>Oui&nbsp;
                        <input type="radio" name="<%=RequestHelper.PARAMETER_WITHCOLLECTIONS%>"
                               value="false"/>Non</font></nobr>
                </td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td width="50%" align="left">
                    <nobr>
                        <font face="arial,helvetica"
                              size="3"><b>Ajouter&nbsp;une&nbsp;bibliothèque&nbsp;&nbsp;</b></font>
                        <a href="#" onclick="javascript:refreshRCR();"><img border="0" src="img/add-icon.png" alt="+"
                                                                            width="22" height="22"/></a>
                    </nobr>
                </td>
                <td width="50%" align="right">
                    <nobr>
                        <font face="arial,helvetica" size="3" color="#000000">
                            <b>
                                <input border="0" src="img/accept.png" type="image" value="submit" align="absmiddle"/>
                                &nbsp;
                                <a style="text-decoration:none;color: #000000;" href="#"
                                   onclick="javascript:document.forms[0].submit();">Valider la Commande</a>
                                &nbsp;&nbsp;
                                <a style="text-decoration:none;color: #000000;" href="#"
                                   onclick="javascript:clearBasket();">
                                    <img border="0" src="img/button_cancel_icon22.gif" alt="Vider le panier" width="22"
                                         height="22" align="absmiddle"/>&nbsp;Vider le panier
                                </a>
                            </b>
                        </font>
                    </nobr>
                </td>
            </tr>
        </table>
        <br/>
        <table id="basket" width="98%" border="0" cellpadding="0" cellspacing="0">
        </table>
    </center>
    <span id="excludedFields"></span>
</form>
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