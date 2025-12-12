package fr.abes.derives.web;

import fr.abes.utils.LogHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutCommand implements ICommand {

    private static LogHelper logger = new LogHelper(LogoutCommand.class);

    public LogoutCommand() {
    }

    public String execute(RequestHelper helper) throws ServletException,
            IOException {

        logger.info(helper, "LogoutCommand");

        HttpSession session = helper.getRequest().getSession(false);
        logger.debug("session = " + session);
        // profile already read in session ?
        UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
        logger.debug("logging out for user " + user);

        ConnectionServlet.logout(helper.getRequest());
        StringBuilder strb = new StringBuilder();
        strb.append(RequestHelper.DOCTYPE)
                .append("<html>")
                .append("<head>")
                .append(RequestHelper.META)
                .append("<title>Déconnexion</title>")
                .append("</head>")
                .append("<body>")
                .append("<div><b>Au revoir</b> ")
                .append(user.getShortName())
                .append(" (")
                .append(RequestHelper.MSG_NOT_AUTHENTICATED)
                .append(")")
                .append("</div><br></br><div><a href=\".\">Retour à la page d'accueil</a></div>")
                .append("</body>")
                .append("</html>");
        RequestHelper.errorUnauthorizedHTML(helper.getResponse(), new Exception(strb.toString()));
        return null;
    }
}
