package com.coderedrobotics.dashboard.dashboard;

/**
 *
 * @author Michael Spoehr
 */
public class Debug {

    public static boolean debugMode = true;
    private static int debugLevel = 3;
    private static boolean errorsDisplayed;
    public static int CRITICAL = 1;
    public static int WARNING = 2;
    public static int STANDARD = 3;
    public static int EXTENDED = 4;
    public static int EVERYTHING = 5;
    public static int DISABLEPRINT = 6;
    // Note: if debug level for incomming print is set to 0, it will override
    // the debug level, even if it is set to 6.

    public static void setDebugLevel(int level) {
        debugLevel = level;
    }

    public static int getDebugLevel() {
        return debugLevel;
    }

    public static boolean isErrorsDisplayed() {
        return errorsDisplayed;
    }

    public static void setErrorsDisplayed(boolean displayErrors) {
        errorsDisplayed = displayErrors;
    }

    public static void disableDebugPrinting() {
        debugLevel = 6;
    }

    public static void print(Object object, int level) {
        if (level <= debugLevel && debugLevel != 6) {
            System.out.print(object);
        } else if (level == 0) {
            System.out.print(object);
        }
    }

    public static void println(Object object, int level) {
        if (level <= debugLevel && debugLevel != 6) {
            System.out.println(object);
        } else if (level == 0) {
            System.out.println(object);
        }
    }

    public static void print(Object object) {
        int level = 0;
        if (level <= debugLevel && debugLevel != 6) {
            System.out.print(object);
        } else if (level == 0) {
            System.out.print(object);
        }
    }

    public static void println(Object object) {
        int level = 0;
        if (level <= debugLevel && debugLevel != 6) {
            System.out.println(object);
        } else if (level == 0) {
            System.out.println(object);
        }
    }

    public static void printErr(Exception e) {
        if (errorsDisplayed) {
            System.err.println(e);
        }
    }

    public static void forcePrintErr(Exception e) {
        System.err.println(e);
    }
}
