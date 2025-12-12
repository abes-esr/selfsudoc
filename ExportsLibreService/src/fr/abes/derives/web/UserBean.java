package fr.abes.derives.web;

public class UserBean {

    final public static String KEY_USER = "USERKEY";
    final public static String KEY_PROFILE = "USERPROFILE";
    private String userKey = null;
    private Integer userNum = null;
    private String shortName = null;
    private String userGroup = null;
    private String library = null;
    private String loginAllowed = null;
    private Integer iln = null;

    public UserBean(
            String userKey,
            Integer userNum,
            String shortName,
            String userGroup,
            String library,
            String loginAllowed,
            Integer iln
    ) {
        super();
        this.userKey = userKey;
        this.userNum = userNum;
        this.shortName = shortName;
        this.userGroup = userGroup;
        this.library = library;
        this.loginAllowed = loginAllowed;
        this.iln = iln;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Login : ")
                .append(userKey)
                .append("<br/>")
                .append("Nom : ")
                .append(shortName)
                .append("<br/>")
                .append("Groupe : ")
                .append(userGroup)
                .append("<br/>")
                .append("ILN : ")
                .append(iln);
        if (isAdmin()) {
            sb.append("<br/>").append("<b>").append("Administrateur : OUI").append("</b>");
        }

        return sb.toString();
    }

    public Integer getIln() {
        return iln;
    }

    public void setIln(Integer iln) {
        this.iln = iln;
    }

    public String getLoginAllowed() {
        return loginAllowed;
    }

    public void setLoginAllowed(String loginAllowed) {
        this.loginAllowed = loginAllowed;
    }

    /**
     *
     * @return user_key format 001
     */
    public boolean isAdmin() {
        String sUserKey = this.userKey.trim().toUpperCase();
        return sUserKey.startsWith("001");
    }
}
