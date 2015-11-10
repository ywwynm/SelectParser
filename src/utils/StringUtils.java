package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张启 on 2015/11/4.
 * Utils for String
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean containsIgnoreCase(String src, String str) {
        return src.toLowerCase().contains(str.toLowerCase());
    }

    public static int indexOfIgnoreCase(String src, String str, int from) {
        return src.toLowerCase().indexOf(str.toLowerCase(), from);
    }

    public static List<Integer> indexesOfIgnoreCase(String src, String str) {
        List<Integer> result = new ArrayList<>();
        int index = -1;
        for (;;) {
            index = indexOfIgnoreCase(src, str, index + 1);
            if (index == -1) {
                break;
            } else {
                result.add(index);
            }
        }
        return result;
    }

    public static List<String> splitIgnoreCase(String src, String str) {
        List<String> result = new ArrayList<>();
        if (str.isEmpty() || !containsIgnoreCase(src, str)) {
            result.add(src);
        } else {
            List<Integer> indexes = indexesOfIgnoreCase(src, str);
            int strLength = str.length();
            int indexesSize = indexes.size();
            for (int i = 0; i < indexesSize - 1; i++) {
                result.add(src.substring(indexes.get(i) + strLength,
                        indexes.get(i + 1)));
            }
            result.add(0, src.substring(0, indexes.get(0)));
            result.add(src.substring(indexes.get(indexesSize - 1) + strLength,
                    src.length()));
        }
        return result;
    }

    public static List<String> multiSplitIgnoreCase(String src, String... strs) {
        List<String> resultList = new ArrayList<>(), tempList;
        resultList.add(src);
        for (String str : strs) {
            tempList = new ArrayList<>();
            for (String result : resultList) {
                List<String> splitResult = splitIgnoreCase(result, str);
                for (String s : splitResult) {
                    if (!s.isEmpty()) {
                        tempList.add(s);
                    }
                }
            }
            resultList = tempList;
        }
        return resultList;
    }

    public static String replaceBetweenIndex(String src, String replacement,
                                             int from, int end) {
        return src.substring(0, from) + replacement + src.substring(end, src.length());
    }
}
