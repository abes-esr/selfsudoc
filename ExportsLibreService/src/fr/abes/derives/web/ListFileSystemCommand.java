package fr.abes.derives.web;

import fr.abes.utils.LogHelper;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;

public class ListFileSystemCommand implements ICommand {

    protected static String DOCBASE = "/applis/selfSudoc";
    private static LogHelper logger = new LogHelper(ListFileSystemCommand.class);

    public ListFileSystemCommand() {
    }

    static protected Set<SimpleImmutableEntry<Date, String>> addFilesnio(Set<SimpleImmutableEntry<Date, String>> set, String root, String dirName) {

        List<Path> result = new ArrayList<Path>();
        Path directory = Paths.get(root + "/" + dirName);

        DirectoryStream<Path> directoryStream = null;
        try {
            directoryStream = Files.newDirectoryStream(directory, "*.{csv,rtf,pdf}");
        } catch (IOException e) {
            logger.error(e);
        }

        if (directoryStream != null) {
            for (Path path : directoryStream) {
                result.add(path);
            }
        }

        for (Path p : result) {
            File f = p.toFile();
            Date date = new Date(f.lastModified());
            String name = p.getFileName().toString();
            set.add(new SimpleImmutableEntry<Date, String>(date, name));
            logger.debug("" + date + "--->" + dirName + "/" + name);
        }

        return set;
    }

    public String execute(RequestHelper helper) throws javax.servlet.ServletException, java.io.IOException {

        logger.info(helper, "ListFileSystemCommand");
        logger.debug("ListFileSystemCommand.DOCBASE=", ListFileSystemCommand.DOCBASE);

        HttpSession session = helper.getRequest().getSession();
        UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
        boolean admin = RequestHelper.isAdmin(user);
        logger.debug("admin=" + admin);

        if (!admin) {
            String message = "<b>Accés restreint</b> (" + RequestHelper.MSG_NOT_ADMINISTRATOR + ")";
            RequestHelper.errorUnauthorizedHTML(helper.getResponse(), new Exception(message));
            return null;
        }

        Set<SimpleImmutableEntry<Date, String>> historique = new HashSet<SimpleImmutableEntry<Date, String>>();
        historique = addFilesnio(historique, ListFileSystemCommand.DOCBASE, RequestHelper.PDF);
        logger.debug("historique pdf=" + historique.size());
        historique = addFilesnio(historique, ListFileSystemCommand.DOCBASE, RequestHelper.RTF);
        logger.debug("historique pdf+rtf=" + historique.size());
        historique = addFilesnio(historique, ListFileSystemCommand.DOCBASE, RequestHelper.SYLK);
        logger.debug("historique pdf+rtf+slk=" + historique.size());

        helper.getRequest().setAttribute("historique", historique);
        logger.debug("historique " + historique.toString());

        MyDispatcher dispatcher = new MyDispatcher();
        return dispatcher.dispatch(this);
    }
}
