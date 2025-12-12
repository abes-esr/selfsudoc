package fr.abes.derives.web;

import fr.abes.utils.LogHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class WelcomeCommand implements ICommand {

    private static LogHelper logger = new LogHelper(WelcomeCommand.class);

    public WelcomeCommand() {
    }

    public String execute(RequestHelper helper) throws ServletException, IOException {

        logger.info(helper, "UserProfileCommand");

        // do nothing
        HttpSession session = helper.getRequest().getSession(false);
        logger.debug("session = " + session);
        // profile already read in session ?
        UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
        logger.debug("user" + user);
        MyDispatcher dispatcher = new MyDispatcher();
        return dispatcher.dispatch(this);
    }
}