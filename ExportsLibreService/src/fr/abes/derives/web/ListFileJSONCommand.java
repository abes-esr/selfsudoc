package fr.abes.derives.web;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ListFileJSONCommand implements ICommand {

    private static LogHelper logger = new LogHelper(ListFileJSONCommand.class);

    public ListFileJSONCommand() {
    }

    public String execute(RequestHelper helper)
            throws javax.servlet.ServletException, java.io.IOException {

        logger.info(helper, "ListFileJSONCommand");
        HttpSession session = helper.getRequest().getSession();
        UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
        boolean admin = RequestHelper.isAdmin(user);
        logger.debug("admin=" + admin);

        if (!admin) {
            String message = RequestHelper.MSG_NOT_ADMINISTRATOR;
            RequestHelper.errorUnauthorizedJSON(helper.getResponse(), new Exception(message));
            return null;
        }

        Set<SimpleImmutableEntry<Date, String>> historique = new HashSet<SimpleImmutableEntry<Date, String>>();
        historique = ListFileSystemCommand.addFilesnio(historique, ListFileSystemCommand.DOCBASE, RequestHelper.PDF);
        historique = ListFileSystemCommand.addFilesnio(historique, ListFileSystemCommand.DOCBASE, RequestHelper.RTF);
        historique = ListFileSystemCommand.addFilesnio(historique, ListFileSystemCommand.DOCBASE, RequestHelper.SYLK);

        JSONArray tab = new JSONArray();

        try {
            for (SimpleImmutableEntry<Date, String> e : historique) {
                JSONObject obj = new JSONObject();
                Date date = e.getKey();
                String filename = e.getValue();
                String format = BufferedRW.extension(filename);
                obj.put("date", date);
                obj.put("format", format);
                obj.put("filename", filename);
                tab.put(obj);
            }
        } catch (JSONException e) {
            logger.error(e.getMessage());
            throw new ServletException(e);
        }

        RequestHelper.flushOkJSON(helper.getResponse(), tab);
        return null; //dispatcher.forward
    }
}
