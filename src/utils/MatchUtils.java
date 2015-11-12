package utils;

/**
 * Created by 张启 on 2015/11/9.
 * utils for matching
 */
public class MatchUtils {

    private MatchUtils() {}

    public static boolean match(int a, String opr, int b) {
        return match((double) a, opr, (double) b);
    }

    public static boolean match(double a, String opr, double b) {
        switch (opr) {
            default:
                return false;
            case "=":
                return a == b;
            case "^":
                return a != b;
            case ">":
                return a > b;
            case "<":
                return a < b;
            case "}":
                return a >= b;
            case "{":
                return a <= b;
        }
    }

    public static boolean match(String a, String opr, String b) {
        switch (opr) {
            default:
                return false;
            case "=":
                return a.equals(b);
            case "^":
                return !a.equals(b);
            case ">":
                return a.compareTo(b) > 0;
            case "<":
                return a.compareTo(b) < 0;
            case "}":
                return a.compareTo(b) >= 0;
            case "{":
                return a.compareTo(b) <= 0;
        }
    }

}
