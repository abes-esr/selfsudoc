<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="fr.abes.derives.web.UserBean" %>
<jsp:include flush="false" page="checkgranted.jsp"></jsp:include>
<%
    UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
%>
<center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr bgcolor="#6e6e6e">
            <td valign="top" width="204" height="74" background="img/produitsderives_01.gif"><br/>
                <nobr>&nbsp;<a style="text-decoration:none;" href="welcome.jsp"><img align="top" src="img/icoprofil.gif"
                                                                                     width="24"
                                                                                     height="17">&nbsp;<em><font
                        size="1" face="arial" color="#ffffff"><%=user.getShortName() %>
                </font></em></a></nobr>
            </td>
            <td valign="top" height="74" background="img/produitsderives_03.gif"></td>
            <td valign="top" width="179" height="74" background="img/evol_produitsderives_03.gif"></td>
            <td valign="top" width="191" height="74" background="img/evol_produitsderives_04.gif"></td>
            <td valign="top" width="143" height="74" background="img/evol_produitsderives_05.gif"></td>
            <td valign="top" height="74" background="img/produitsderives_03.gif"></td>
            <td valign="top" align="right" width="181" height="74" background="img/produitsderives_06.gif"><br/>
                <nobr><a style="text-decoration:none;" href="logout"><font size="1" face="arial" color="#ffffff">D&eacute;connexion</font>&nbsp;<img
                        src="img/quit_icon.png" align="top" border="0" width="17" height="17"/></a>&nbsp;
                </nobr>
            </td>
        </tr>
        <tr bgcolor="#ffffff">
            <td valign="top" width="204" height="35"><img src="img/produitsderives_07.gif" width="204" height="35"
                                                          border="0"></td>
            <td valign="top" width="225" height="35"><a href="add.jsp"><img src="img/evol_produitsderives_09.gif"
                                                                            width="225" height="35" border="0"
                                                                            alt="Publications en série"></a></td>
            <td valign="top" width="179" height="35"><a href="orders.jsp"><img src="img/evol_produitsderives_10.gif"
                                                                               border="0" width="179" height="35"
                                                                               alt="Historique"></a></td>
            <td valign="top" width="191" height="35"><a href="autres.jsp"><img src="img/evol_produitsderives_11.gif"
                                                                               border="0" width="191" height="35"
                                                                               alt="Autres services"></a></td>
            <td valign="top" colspan="2" height="35" background="img/produitsderives_10.gif"></td>
            <td valign="top" width="181"><a href="http://documentation.abes.fr/aideselfsudoc/index.html"><img
                    src="img/produitsderives_12.gif" width="181" height="35" width="181" height="35" border="0"
                    alt="Aide"></a></td>
        </tr>
        <tr bgcolor="#bebebe">
            <td valign="top" colspan="7" height="75" background="img/produitsderives_13.gif"></td>
        </tr>
    </table>
</center>