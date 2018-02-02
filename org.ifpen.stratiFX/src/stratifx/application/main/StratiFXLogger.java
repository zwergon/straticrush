package stratifx.application.main;

import fr.ifp.kronosflow.utils.ILogger;

public class StratiFXLogger implements ILogger {

    static public int ERROR = 0;
    static public int WARNING = 1;
    static public int INFO = 2;
    static public int DEBUG = 3;

    private static int logLevel = DEBUG;

    /**
     * Get the current line number.
     *
     * @return int - Current line number.
     */
    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[3].getLineNumber();
    }

    public boolean isDebugEnabled(Class<?> clazz) {
        return logLevel >= DEBUG;
    }

    public boolean isDebugEnabled() {
        return logLevel >= DEBUG;
    }

    public boolean isInfoEnabled() {
        return logLevel >= INFO;
    }

    public boolean isWarningEnabled() {
        return logLevel >= WARNING;
    }

    public void debug(String s, Class<?> clazz) {

        if (isDebugEnabled()) {
            System.out.println((char)27 + "[36mD " + clazz.getName() + " - " + getLineNumber() + " : " + s + (char)27 + "[0m");
        }
    }

    public void info(String s, Class<?> clazz) {
        if (isInfoEnabled()) {
            System.out.println((char)27 + "[32mI " + clazz.getName() + " - " + getLineNumber() + " : " + s + (char)27 + "[0m");
        }
    }

    public void warning(String s, Class<?> clazz) {
        if (isWarningEnabled()) {
            System.out.println("W " + clazz.getName() + " - " + getLineNumber() + " : " + s);
        }
    }

    public void warning(Exception e, Class<?> clazz) {
        warning(e.getMessage(), clazz);
    }

    public void error(String s, Class<?> clazz) {
        System.err.println("-EEE-" + clazz.getName() + " - " + getLineNumber() + " : " + s);
    }

    public void error(Exception e, Class<?> clazz) {
        error(e.getMessage(), clazz);
    }

}
