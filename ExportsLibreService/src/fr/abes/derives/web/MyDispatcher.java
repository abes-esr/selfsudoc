package fr.abes.derives.web;

import fr.abes.utils.LogHelper;


public class MyDispatcher {

    final public static String AUTH_ERROR_PAGE = "/error.jsp";
    private static LogHelper logger = new LogHelper(MyDispatcher.class);

    public String dispatch(ICommand command) {

        String className = command.getClass().getName();
        String result = null;

        logger.debug("dispatch=" + className);

        if (WelcomeCommand.class.getName().equals(className)) {
            result = "/welcome.jsp";
        }
        if (ListFileSystemCommand.class.getName().equals(className)) {
            result = "/files.jsp";
        }
        if (AddOrderCommand.class.getName().equals(className)) {
            result = "/orderprogress.jsp";
        }
        if (ListFileJSONCommand.class.getName().equals(className)) {
            result = null;
        } //no page for json response
        if (ListRcrJSONCommand.class.getName().equals(className)) {
            result = null;
        } //no page for json response
        if (OrdersJSONCommand.class.getName().equals(className)) {
            result = null;
        } //no page for json response
        if (AcceptTermCommand.class.getName().equals(className)) {
            result = "/welcome.jsp";
        } //no page for json response

        logger.debug("result=" + result);

        return result;
    }
}