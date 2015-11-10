package core;

import utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 张启 on 2015/11/3.
 * Split elements from a select-sentence.
 */
public class ParamsSplitter {

    static final String SELECT = "select";
    static final String FROM   = "from";
    static final String WHERE  = "where";

    private String mSelectedParams;
    private String mFromParams;
    private String mWhereParams;

    public static ParamsSplitter newInstance(String selectedStr) {
        // todo: check if selectedStr is valid
        if (selectedStr == null || selectedStr.isEmpty()) {
            return null;
        }

        boolean hasMultiSelect =
                selectedStr.indexOf(SELECT) != selectedStr.lastIndexOf(SELECT);
        boolean hasMultiFrom =
                selectedStr.indexOf(FROM) != selectedStr.lastIndexOf(FROM);
        boolean hasMultiWhere =
                selectedStr.indexOf(WHERE) != selectedStr.lastIndexOf(WHERE);
        if (hasMultiSelect || hasMultiFrom || hasMultiWhere) {
            return null;
        }

        return new ParamsSplitter(selectedStr);
    }

    public List<String> splitSelectedParams() {
        if (mSelectedParams == null) {
            return null;
        }

        String params = mSelectedParams.replaceAll(" ", "");
        String[] result = params.split(",");
        return Arrays.asList(result);
    }

    public List<String> splitWhereParams() {
        if (mWhereParams == null || mWhereParams.isEmpty()) {
            return null;
        }

        mWhereParams = replaceOperatorWithSignal(mWhereParams, "and", "&");
        mWhereParams = replaceOperatorWithSignal(mWhereParams, "or", "|");
        mWhereParams = replaceOperatorWithSignal(mWhereParams, "not", "!");

        System.out.println(mWhereParams);

        List<String> result = new ArrayList<>();
        int length = mWhereParams.length();
        char c;
        int bracketIndex;
        for (int i = 0; i < length;) {
            c = mWhereParams.charAt(i);
            if (c != ' ' && c != '(' && c != ')'
                    && c != '&' && c != '|' && c != '!') {
                bracketIndex = mWhereParams.indexOf(")", i);
                result.add(mWhereParams.substring(i, bracketIndex) + ":" + i);
                i = bracketIndex + 1;
            } else {
                i++;
            }
        }
        return result;
    }

    public String[] splitBinaryExpression(String exp) {
        if (exp == null || exp.isEmpty()) {
            return null;
        }
        String[] result = new String[3];
        int length = exp.length();
        char c;
        for (int i = 1; i < length; i++) {
            c = exp.charAt(i);
            if (c == '=' || c == '<' || c == '>') {
                result[0] = exp.substring(0, i);
                if (i + 1 >= length) {
                    return null;
                } else {
                    c = exp.charAt(i + 1);
                    if (c == '=' || c == '<' || c == '>') {
                        result[1] = exp.substring(i, i + 2);
                        result[2] = exp.substring(i + 2, length);
                    } else {
                        result[1] = exp.substring(i, i + 1);
                        result[2] = exp.substring(i + 1, length);
                    }
                }
                break;
            }
        }
        return result;
    }

    public String getSelectedParams() {
        return mSelectedParams;
    }

    public String getFromParams() {
        return mFromParams;
    }

    public String getWhereParams() {
        return mWhereParams;
    }

    private ParamsSplitter(String selectStr) {
        List<String> params = StringUtils.multiSplitIgnoreCase(
                selectStr, SELECT, FROM, WHERE);
        deleteOuterBrackets(params);
        mSelectedParams = params.get(0);
        mFromParams = params.get(1);
        if (params.size() < 3) {
            mWhereParams = "";
        } else {
            mWhereParams = params.get(2);
        }
    }

    private void deleteOuterBrackets(List<String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        int size = params.size();
        String param;
        int first, last;
        for (int i = 0; i < size; i++) {
            param = params.get(i);
            first = param.indexOf("(");
            last  = param.lastIndexOf(")");
            param = param.substring(first + 1, last);
            params.set(i, param);
        }
    }

    private String replaceOperatorWithSignal(String where, String opr, String signal) {
        return where
                .replaceAll(" " + opr + " ", " " + signal + " ")
                .replaceAll("\\(" + opr, "\\(" + signal)
                .replaceAll("\\)" + opr, "\\)" + signal)
                .replaceAll(opr + "\\(", signal + "\\(");
    }
}
