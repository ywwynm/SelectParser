package core;

/**
 * Created by 张启 on 2015/11/9.
 * operator like '|', '&' and '!'
 */
public class LogicOperator implements Comparable<LogicOperator> {
    
    private char mOpr;
    private int mLevel;

    public static boolean isLogicOperator(char ch) {
        return ch == '#' || ch == '(' || ch == ')' || ch == '|'
                || ch == '&' || ch == '!';
    }

    public LogicOperator(char opr) {
        if (opr == '#') {
            mLevel = -1;
        } else if (opr == '(' || opr == ')') {
            mLevel = 0;
        } else if (opr == '|') {
            mLevel = 1;
        } else if (opr == '&') {
            mLevel = 2;
        } else if (opr == '!') {
            mLevel = 3;
        }
        mOpr = opr;
    }

    public char toChar() {
        return mOpr;
    }

    public int getLevel() {
        return mLevel;
    }

    @Override
    public int compareTo(LogicOperator another) {
        if (mLevel < another.mLevel) {
            return -1;
        } else if (mLevel == another.mLevel) {
            return 0;
        } else {
            return 1;
        }
    }
}
