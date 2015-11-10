package core;

import java.util.Stack;

/**
 * Created by 张启 on 2015/11/9.
 * logic calculator
 */
public class LogicCalculator {

    private LogicCalculator() {}

    public static boolean calculate(String exp) {
        String finalExp = exp.replaceAll("\\(f\\)", "f");
        finalExp = finalExp.replaceAll("\\(t\\)", "t");
        finalExp = finalExp.replaceAll("\\(!t\\)", "f");
        finalExp = finalExp.replaceAll("\\(!f\\)", "t");
        finalExp += '#';

        Stack<LogicOperator> operators = new Stack<>();
        operators.push(new LogicOperator('#'));

        Stack<Boolean> booleans = new Stack<>();

        int length = finalExp.length();
        char c, lastBool = 0;
        for (int i = 0; i < length; i++) {
            c = finalExp.charAt(i);
            if (LogicOperator.isLogicOperator(c)) {
                if (lastBool != 0) {
                    booleans.push(lastBool == 't');
                    lastBool = 0;
                }

                LogicOperator curOpr = new LogicOperator(c);
                while (!operators.empty()) {
                    LogicOperator topOpr = operators.pop();

                    int cmp = topOpr.compareTo(curOpr);
                    if (cmp < 0 || c == '(') {
                        operators.push(topOpr);
                        operators.push(curOpr);
                        break;
                    } else if (cmp > 0 || (cmp == 0 && topOpr.getLevel() > 0)) {
                        char opr = topOpr.toChar();
                        if (opr == '!') {
                            booleans.push(!booleans.pop());
                        } else {
                            boolean b2 = booleans.pop();
                            boolean b1 = booleans.pop();
                            booleans.push(calculate(b1, opr, b2));
                        }
                    } else if (cmp == 0) {
                        if (topOpr.getLevel() == 0) {
                            break;
                        }
                    } else {
                        operators.push(topOpr);
                    }
                }
            } else {
                lastBool = c;
            }
        }

        return booleans.pop();
    }

    private static boolean calculate(boolean b1, char opr, boolean b2) {
        switch (opr) {
            default:
                return false;
            case '|':
                return b1 || b2;
            case '&':
                return b1 && b2;
        }
    }
}
