<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.Set" %>
<%@page import="java.util.AbstractMap.SimpleImmutableEntry" %>
<%@page import="fr.abes.utils.BufferedRW" %>
<jsp:include flush="false" page="checkgranted.jsp"></jsp:include>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">
    <link rel="stylesheet" type="text/css" href="clusterize.css"/>
    <script src="js/clusterize.min.js"></script>
    <title>Fichiers sur disque - SELF Sudoc - Service En Ligne de Fichiers Sudoc</title>
</head>
<body>
<center>
    <table width="100%" cellspacing="0" cellpadding="0" border="0">
        <tr>
            <td with="100%" align="center">
                <form name="listfiles" method="post" action="listfiles">
                    <input type="submit" value="Raffraichir"/>
                </form>
            </td>
        </tr>
    </table>
</center>


<div><br></br><b>Liste des fichiers sur disque</b></div>

<div class="clusterize">
    <table width="100%" cellspacing="0" cellpadding="0" border="0">
        <thead>
        <tr>
            <th width="10%" align="left"><b>Num</b></th>
            <th width="30%" align="left"><b>FICHIER</b></th>
            <th width="30%" align="left"><b>DATE</b></th>
            <th width="30%" align="left"><b>FORMAT</b></th>
        </tr>
        </thead>
    </table>
    <div id="scrollArea" class="clusterize-scroll">
        <table width="100%" cellspacing="0" cellpadding="0" border="0">
            <tbody id="contentArea" class="clusterize-content">
            <%
                Set<SimpleImmutableEntry<Date, String>> setFiles = (Set<SimpleImmutableEntry<Date, String>>) request
                        .getAttribute("historique");

                if (setFiles != null) {
                    int count = 0;
                    for (SimpleImmutableEntry<Date, String> e : setFiles) {
                        Date date = e.getKey();
                        String filename = e.getValue();
                        String format = BufferedRW.extension(filename);
                        if (count % 2 == 1) {
                            out.print("<tr class=\"clusterize-no-data\" style=\"background-color: #cccccc;\" >");
                        } else {
                            out.print("<tr class=\"clusterize-no-data\">");
                        } %>
            <td width="10%" align="left"><%=count %>
            </td>
            <td width="30%" align="left">
                <nobr><%=filename %>
                </nobr>
            </td>
            <td width="30%" align="left"><%=date %>
            </td>
            <td width="30%" align="left"><%=format %>
            </td>
            </tr>
            <%
                        count++;
                    }
                }
            %>

            </tbody>
        </table>
    </div>
</div>


<script type="text/javascript">

    var clusterize = new Clusterize({
        scrollId: 'scrollArea',
        contentId: 'contentArea'
    });

</script>
</body>
</html>