package fr.abes.derives.web;

import fr.abes.utils.LogHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AcceptTermCommand implements ICommand {

    private static LogHelper logger = new LogHelper(AcceptTermCommand.class);

    private Integer secretCandidate = null;

    public AcceptTermCommand(Integer secretCandidate) {
        this.secretCandidate = secretCandidate;
    }

    /**
     * put flag in session
     *
     */
    public String execute(RequestHelper helper) throws ServletException,
            IOException {

        logger.info(helper, "AcceptTermCommand");

        HttpSession session = helper.getRequest().getSession();

        if (this.secretCandidate == null) {
            logger.warn("secretCandidate is null");
        } else {

            logger.debug("secretCandidate=", Integer.toString(this.secretCandidate));
            Integer sharedSecret = (Integer) session
                    .getAttribute(RequestHelper.KEY_SHAREDSECRET);

            if (!sharedSecret.equals(this.secretCandidate)) {
                // shared is not the same !
                logger.warn("secretCandidate is not sharedSecret !");
            } else {
                logger.info("secret is shared, terms accepted");
                session.setAttribute(RequestHelper.KEY_ACCEPTTERMS, true);

            }

        }
        MyDispatcher dispatcher = new MyDispatcher();
        return dispatcher.dispatch(this);

    }

}
