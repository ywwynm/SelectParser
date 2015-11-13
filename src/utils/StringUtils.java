package utils;

/**
 * Created by 张启 on 2015/11/13.
 * Utils for {@link String}
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean isEmptyString(String s) {
        return s == null || s.isEmpty();
    }

    public static String replaceSubstring(String src, String replacement,
                                              int from, int end) {
        return src.substring(0, from) + replacement + src.substring(end, src.length());
    }

}
