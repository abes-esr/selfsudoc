package fr.abes.derives.web;

import fr.abes.utils.LogHelper;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashRedirectServlet extends HttpServlet {

    final static String SQL = "select id,rcr from produits_derives.demandes where md5hash=?";
    /**
     * Remove MD5 obfuscation from request by querying DataBase and redirect to file to serve
     */
    private static final long serialVersionUID = 4974137915514932606L;
    final private static String REGEX = "/download/(.*)";
    final private static Pattern PATTERN = Pattern.compile(REGEX);
    final private static String REGEX_EXTENSION = "(.+)\\.(.+)";
    final private static Pattern PATTERN_EXTENSION = Pattern.compile(REGEX_EXTENSION);
    private static DataSource produitsDerivesDataSource = null;
    private static String loginProduitsDerives = null;
    private static String passwordProduitsDerives = null;
    private static String runtimeDir = null;
    private static LogHelper logger = new LogHelper(HashRedirectServlet.class);

    private static void release(ResultSet resultSet, PreparedStatement statement) {

        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     *
     * return last group matching (.+).(.+) pattern = part after the dot
     *
     * @param filename
     * @return
     */
    private static String extension(String filename) {

        Matcher matcher = PATTERN_EXTENSION.matcher(filename);

        String ext = null;

        if (matcher.find()) {
            // last group
            int last = matcher.groupCount();
            ext = matcher.group(last);
        }

        return ext;
    }

    private static String fileNameFromDB(String md5) {

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            if (produitsDerivesDataSource != null) {
                conn = produitsDerivesDataSource.getConnection(loginProduitsDerives, passwordProduitsDerives);
            }

            conn.setAutoCommit(false);

            statement = conn.prepareStatement(SQL);

            Map<Integer, String> params = new HashMap<Integer, String>();
            params.put(1, md5);

            Set<Integer> keys = params.keySet();
            for (Integer i : keys) {
                statement.setString(i, params.get(i));
            }

            resultSet = statement.executeQuery();

            String fileName = null;

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String rcr = resultSet.getString("rcr");

                fileName = "notices_" + rcr + "_" + id;
            }

            conn.commit();

            return fileName;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            release(resultSet, statement);
            resultSet = null;
            statement = null;

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            conn = null;
        }
    }

    public void init() throws ServletException {

        Context envContext = null;

        try {
            // recuperation de la source de donne
            Context initContext = new InitialContext();
            envContext = (Context) initContext.lookup("java:/comp/env");

            produitsDerivesDataSource = (DataSource) envContext.lookup("jdbc/ProduitsDerivesDataSource");
            loginProduitsDerives = (String) envContext.lookup("loginProduitsDerives");
            passwordProduitsDerives = (String) envContext.lookup("passwordProduitsDerives");
            runtimeDir = (String) envContext.lookup("runtimeDir") + File.separator + "docBase";

        } catch (NamingException e) {
            logger.error(e.getMessage());
            throw new ServletException(e);
        }

        logger.info("loginProduitsDerives=", loginProduitsDerives);

        if (envContext == null)
            throw new ServletException("Error: No Context");
        if (loginProduitsDerives == null)
            throw new ServletException("Error: No loginProduitsDerives");
        if (passwordProduitsDerives == null)
            throw new ServletException("Error: No passwordProduitsDerives");
        if (produitsDerivesDataSource == null)
            throw new ServletException("Error: No produitsDerivesDataSource");
        if (runtimeDir == null)
            throw new ServletException("Error: No runtimeDir");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    /*
     *  !!! We MUST use in URI filename to force IE recognize content type, it will not work using request parameter (?xxx=yyyy)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final String CSV = "csv";

        String uri = req.getRequestURI();
        logger.debug("URI : " + uri);

        Matcher matcher = PATTERN.matcher(uri);

        if (matcher.find()) {

            String queriedAfterMapping = matcher.group(1);
            logger.debug("queriedAfterMapping : " + queriedAfterMapping);

            if (queriedAfterMapping == null || "".equals(queriedAfterMapping.trim())) {
                // do nothing
            } else {
                queriedAfterMapping = queriedAfterMapping.trim();

                String filename = null;
                String md5WithExtension = null;
                String extension = null;

                md5WithExtension = queriedAfterMapping;
                // retrieve filename and extension from MD5 hash with extension
                extension = extension(md5WithExtension);
                // before the dot
                int dotPos = md5WithExtension.lastIndexOf("." + extension);

                String md5 = md5WithExtension.substring(0, dotPos);
                filename = fileNameFromDB(md5);

                if (filename == null) {
                    ///no match in DB with this hash
                    logger.warn("no match in DB with this hash");
                } else {
                    //file match in DB
                    String uriExtension = ("slk".equals(extension)) ? CSV : extension;
                    String uriRedirected = "/" + extension + "/" + filename + "." + uriExtension;

                    logger.debug("Client wants to access document : ", filename + "." + extension);
                    logger.debug("Application will serve : " + uriRedirected);

                    RequestDispatcher dispatcher = getServletContext()
                            .getRequestDispatcher(uriRedirected);
                    //correct way for IE attachments
                    resp.addHeader("Content-Disposition", "attachment; filename=" + filename + "." + uriExtension);

                    /*
                     * we no more redirect, now we read from filesystem and copy in response outputStream
                     *  dispatcher.forward(req, resp);
                     */

                    File file = new File(runtimeDir + uriRedirected);
                    if (!file.exists()) {
                        logger.error("File does not exist " + file.getPath());
                        throw new FileNotFoundException(file.getPath());
                    }
                    InputStream input = new FileInputStream(file);

                    String mimeType = getServletContext().getMimeType(file.getPath());
                    if (mimeType == null) {
                        // set to binary type if MIME mapping not found
                        mimeType = "application/octet-stream";
                    }
                    logger.debug("MIME type: " + mimeType);
                    resp.setContentType(mimeType);

                    OutputStream out = resp.getOutputStream();

                    try {
                        byte[] buffer = new byte[4096];
                        int bytesRead = -1;

                        while ((bytesRead = input.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("read/write error : ", e.getMessage());
                    } finally {

                        if (input != null) {
                            try {
                                input.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                logger.error("input error : ", e.getMessage());
                            }
                        }

                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                logger.error("response error : ", e.getMessage());
                            }
                        }
                    }
                }
            }
        } else {
            logger.debug("no match on URI", uri);
        }
    }
}
