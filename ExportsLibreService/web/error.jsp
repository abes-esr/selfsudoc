<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<%
    String errorMsg = (String) request.getAttribute(RequestHelper.KEY_AUTH_ERROR_MSG);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">
    <title></title>
    <!-- no title for low profile -->
</head>
<body>
<h2>Echec de l'authentifciation !</h2>
<%
    if (errorMsg != null) {
        out.write("<div><font color=\"#ff0000\" face=\"arial\">" + errorMsg + "</font></div>");
    }
%>
<div><br></br><a href="." rel="nofollow">Nouvelle tentative ?</a></div>
</body>
</html>