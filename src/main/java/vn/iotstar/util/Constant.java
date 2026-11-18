package vn.iotstar.util;

public class Constant {
    public static final String DIR = System.getProperty("catalina.base", ".") + "/data/category";
    public static final String VIDEO_DIR = System.getProperty("catalina.base", ".") + "/data/video";
    public static final String DEFAULT_FILENAME = "default.file";
    public static final String SESSION_USERNAME = "username";
    public static final String COOKIE_REMEMBER = "username";

    public static class Path {
        public static final String LOGIN = "/views/login.jsp";
        public static final String REGISTER = "/views/register.jsp";
    }
}
