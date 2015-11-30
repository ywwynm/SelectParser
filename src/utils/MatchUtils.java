package utils;

import java.util.LinkedHashMap;

/**
 * Created by 张启 on 2015/11/9.
 * Utils for matching.
 */
public class MatchUtils {

    private static String INTEGER = "integer";
    private static String DOUBLE  = "double";
    private static String VARCHAR = "varchar";

    private MatchUtils() {}

    /**
     * judge if value's type matches given field type or if they can compare
     * with each other by type cast.
     * The reason why {@param data} and {@param fieldName} exist is that we
     * should update the value to correct type even it can compare by type cast.
     */
    public static boolean isValueMatchType(Object value, String fieldType,
                                           LinkedHashMap<String, Object> data,
                                           String fieldName) {
        if (INTEGER.equals(fieldType)) {
            return isValueMatchInteger(value);
        } else if (DOUBLE.equals(fieldType)) {
            return isValueMatchDouble(value, data, fieldName);
        } else if (VARCHAR.equals(fieldType)) {
            return isValueMatchVarchar(value);
        }
        return false;
    }

    public static boolean isValueMatchInteger(Object value) {
        return value instanceof Integer;
    }

    /**
     * judge if value's type is double or integer. If it's integer,
     * cast it to double and put the value to {@param data}
     */
    public static boolean isValueMatchDouble(Object value,
                                             LinkedHashMap<String, Object> data,
                                             String fieldName) {
        if (value instanceof Double) {
            return true;
        }
        if (value instanceof Integer) {
            int trueValue = (Integer) value;
            data.put(fieldName, (double) trueValue);
            return true;
        }
        return false;
    }

    public static boolean isValueMatchVarchar(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        String trueValue = (String) value;
        return trueValue.startsWith("'") && trueValue.endsWith("'");
    }

    public static int isValueMatchComparison(Object value, String opr,
                                             String arg, String type) {
        if (INTEGER.equals(type)) {
            return isValueMatchComparison((int) value, opr, arg);
        } else if (DOUBLE.equals(type)) {
            return isValueMatchComparison((double) value, opr, arg);
        } else if (VARCHAR.equals(type)) {
            return isValueMatchComparison((String) value, opr, arg);
        }
        return -1;
    }

    /**
     * judge if integer value matches the comparison.
     * Since integer value can compare with double value, we should not
     * stop when finding the {@param arg} is not integer.
     */
    public static int isValueMatchComparison(int value, String opr, String arg) {
        Integer argInt;
        try {
            argInt = Integer.valueOf(arg);
            return matchComparison(value, opr, argInt) ? 1 : 0;
        } catch (NumberFormatException e) {
            Double argDbl;
            try {
                argDbl = Double.valueOf(arg);
                return matchComparison(value, opr, argDbl) ? 1 : 0;
            } catch (NumberFormatException another) {
                return -1;
            }
        }
    }

    public static int isValueMatchComparison(double value, String opr, String arg) {
        Double argDbl;
        try {
            argDbl = Double.valueOf(arg);
            return MatchUtils.matchComparison(value, opr, argDbl) ? 1 : 0;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int isValueMatchComparison(String value, String opr, String arg) {
        if (arg.startsWith("'") && arg.startsWith("'")) {
            return matchComparison(value.replaceAll("'", ""),
                    opr, arg.replaceAll("'", "")) ? 1 : 0;
        }
        return -1;
    }

    public static boolean isOperatorValid(String opr) {
        return "=".equals(opr) || "!".equals(opr) || "<".equals(opr) || ">".equals(opr)
                || "{".equals(opr) || "}".equals(opr);
    }

    public static boolean matchComparison(int a, String opr, int b) {
        return matchComparison((double) a, opr, (double) b);
    }

    public static boolean matchComparison(double a, String opr, double b) {
        if ("=".equals(opr)) {
            return a == b;
        } else if ("!".equals(opr)) {
            return a != b;
        } else if (">".equals(opr)) {
            return a > b;
        } else if ("<".equals(opr)) {
            return a < b;
        } else if ("}".equals(opr)) {
            return a >= b;
        } else if ("{".equals(opr)) {
            return a <= b;
        }
        return false;
    }

    public static boolean matchComparison(String a, String opr, String b) {
        if ("=".equals(opr)) {
            return a.equals(b);
        } else if ("!".equals(opr)) {
            return !a.equals(b);
        } else if (">".equals(opr)) {
            return a.compareTo(b) > 0;
        } else if ("<".equals(opr)) {
            return a.compareTo(b) < 0;
        } else if ("}".equals(opr)) {
            return a.compareTo(b) >= 0;
        } else if ("{".equals(opr)) {
            return a.compareTo(b) <= 0;
        }
        return false;
    }

}
