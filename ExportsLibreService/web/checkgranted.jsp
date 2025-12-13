<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="fr.abes.derives.web.ConnectionServlet" %>
<%@page import="fr.abes.derives.web.RequestHelper" %>
<%@page import="fr.abes.derives.web.MyDispatcher" %>
<%@page import="fr.abes.derives.web.UserBean" %>
<%
    //If no session, or no user, or user not granted, redirect to auth error page
    if (session == null) {
        ConnectionServlet.logout(request);
        RequestHelper.setResponseUnauthorizedHTML(response);
        request.setAttribute(RequestHelper.KEY_AUTH_ERROR_MSG, RequestHelper.MSG_SESSION_EXPIRED);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(MyDispatcher.AUTH_ERROR_PAGE);
        dispatcher.forward(request, response);
    } else {
        UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
        if (user == null) {
            ConnectionServlet.logout(request);
            RequestHelper.setResponseUnauthorizedHTML(response);
            request.setAttribute(RequestHelper.KEY_AUTH_ERROR_MSG, RequestHelper.MSG_NOT_AUTHENTICATED);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(MyDispatcher.AUTH_ERROR_PAGE);
            dispatcher.forward(request, response);
        } else {
            if (!RequestHelper.isGranted(user)) {
                ConnectionServlet.logout(request);
                RequestHelper.setResponseUnauthorizedHTML(response);
                request.setAttribute(RequestHelper.KEY_AUTH_ERROR_MSG, RequestHelper.MSG_NOT_GRANTED + " : " + user);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(MyDispatcher.AUTH_ERROR_PAGE);
                dispatcher.forward(request, response);
            }
        }
    }
%>