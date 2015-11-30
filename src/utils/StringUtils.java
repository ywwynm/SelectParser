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

    public static boolean containsIgnoreCase(String src, String dst) {
        return src.toLowerCase().contains(dst.toLowerCase());
    }

    public static String replaceAll(String src, String dst, String replacement) {
        if (!containsIgnoreCase(src, dst)) {
            return src;
        }

        int srcLen = src.length();
        int dstLen = dst.length();
        for (int s = 0; s < srcLen; s++) {
            int e = s + dstLen;
            String sub = src.substring(s, e);
            if (sub.equals(dst)) {
                return replaceAll(replaceSubstring(src, replacement, s, e),
                        dst, replacement);
            }
        }
        return src;
    }

}
