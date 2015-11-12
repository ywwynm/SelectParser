package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by 张启 on 2015/11/3.
 * Split elements from a select sentence into different parts.
 * Firstly, separate "select", "from", "where" and their parameters.
 * Then, split parameters after "select" if necessary.
 * Finally, focus on parameters after "where" and split them into smaller
 * and atomic expression.
 * Besides, the class has the ability to split binary expression into
 * an array whose elements are column name, comparison operator and constant.
 */
public class ParamsSplitter {

    private String mSelectParams;
    private String mFromParams;
    private String mWhereParams;

    public static ParamsSplitter newInstance(String selectedStr) {
        if (selectedStr == null || selectedStr.isEmpty()) {
            return null;
        }
        return new ParamsSplitter(selectedStr);
    }

    /**
     * Get names of selected columns after "from".
     * @return names of selected columns.
     */
    public List<String> splitSelectParams() {
        if (mSelectParams == null || mSelectParams.isEmpty()) {
            return null;
        }

        String params = mSelectParams.replaceAll(" ", "");
        String[] result = params.split(",");
        return Arrays.asList(result);
    }

    /**
     * Get atomic expressions after "where" with a special format like
     * "<identifier> <operator> <constant>:0".
     * In order to simplify the problem, the function will change original
     * expression after "where" by replacing logic operators in words("and",
     * "or" and "not") with signals("&", "|" and "!"). Then split will
     * happen for this expression.
     * After splitting, smaller expressions will start with original atomic
     * expression and end with a colon and its index of changed expression
     * so that we can replace them with their value(true or false) for a
     * given row in the future.
     * @return expressions after "where".
     */
    public List<String> splitWhereParams() {
        if (mWhereParams == null || mWhereParams.isEmpty()) {
            return null;
        }

        // Get rid of useless whitespaces.
        mWhereParams = mWhereParams.replaceAll(" ", "");

        /*
            We have already deleted the outer brackets. If there aren't
            any left bracket still, there should be only one expression
            after "where".
         */
        if (!mWhereParams.contains("(")) {
            return Collections.singletonList(mWhereParams + ":0");
        }

        mWhereParams = replaceOperatorWithSignal(mWhereParams, "and", "&");
        mWhereParams = replaceOperatorWithSignal(mWhereParams, "or",  "|");
        mWhereParams = replaceOperatorWithSignal(mWhereParams, "not", "!");

        List<String> result = new ArrayList<>();
        int length = mWhereParams.length();
        char c;
        /*
            Thanks to requirement, every expression should end with a right
            bracket. We can use this character to decide what substrings
            are useful expressions.
         */
        int bracketIndex;
        for (int i = 0; i < length;) {
            c = mWhereParams.charAt(i);
            if (c != '(' && c != ')'&& c != '&' && c != '|' && c != '!') {
                bracketIndex = mWhereParams.indexOf(")", i);
                result.add(mWhereParams.substring(i, bracketIndex) + ":" + i);
                i = bracketIndex + 1;
            } else {
                i++;
            }
        }
        return result;
    }

    /**
     * Split a binary expression into an array whose elements are column
     * name, comparison operator and constant. For example, expression
     * "a<=0" will be changed and separated into "a","{" and "0".
     * @param exp the binary expression to split.
     * @return an array whose elements are column name, comparison
     * operator and constant.
     */
    public String[] splitBinaryExpression(String exp) {
        if (exp == null || exp.isEmpty()) {
            return null;
        }
        exp = exp.replaceAll("<=", "{");
        exp = exp.replaceAll(">=", "}");
        exp = exp.replaceAll("<>", "^");
        String[] result = new String[3];
        int length = exp.length();
        char cur;
        for (int i = 1; i < length; i++) {
            cur = exp.charAt(i);
            if (isComparisonOperator(cur)) {
                result[0] = exp.substring(0, i);
                result[1] = exp.substring(i, i + 1);
                result[2] = exp.substring(i + 1, length);
                break;
            }
        }
        return result;
    }

    public String getSelectParams() {
        return mSelectParams;
    }

    public String getFromParams() {
        return mFromParams;
    }

    public String getWhereParams() {
        return mWhereParams;
    }

    private ParamsSplitter(String selectStr) {
        selectStr = selectStr.trim();

        List<String> params = new ArrayList<>();

        int fl = selectStr.indexOf("("); // first left bracket
        int fr = selectStr.indexOf(")"); // first right bracket
        params.add(selectStr.substring(fl + 1, fr));

        int sl = selectStr.indexOf("(", fl + 1); // second left bracket
        int sr = selectStr.indexOf(")", sl); // second right bracket
        params.add(selectStr.substring(sl + 1, sr));

        int tl = selectStr.indexOf("(", sl + 1); // third left bracket
        if (tl != -1) {
            int lr = selectStr.lastIndexOf(")"); // last right bracket
            params.add(selectStr.substring(tl + 1, lr));
        }

        mSelectParams = params.get(0);
        mFromParams = params.get(1);
        if (tl == -1) { // no "where"
            mWhereParams = "";
        } else {
            mWhereParams = params.get(2);
        }
    }

    private boolean isComparisonOperator(char c) {
        return c == '=' || c == '<' || c == '>'
                || c == '{' || c == '}' || c == '^';
    }

    private String replaceOperatorWithSignal(String where, String opr, String signal) {
        return where
                .replaceAll(" " + opr + " ", " " + signal + " ")
                .replaceAll("\\(" + opr, "\\(" + signal)
                .replaceAll("\\)" + opr, "\\)" + signal)
                .replaceAll(opr + "\\(", signal + "\\(");
    }
}
