package core;

import java.util.Stack;

/**
 * Created by 张启 on 2015/11/9.
 * Calculator for "and", "or" and "not".
 */
public class LogicCalculator {

    private LogicCalculator() {}

    /**
     * Calculate "and", "or" and "not" inside the expression and get
     * the final result.
     * @param exp the expression to calculate.
     * @return the result of this expression.
     */
    public static boolean calculate(String exp) {
        // Delete useless brackets.
        String finalExp = exp.replaceAll("\\(f\\)", "f");
        finalExp = finalExp.replaceAll("\\(t\\)", "t");

        // Try to simplify the expression by directly changing
        // separate "!f" or "!t" into their true values.
        finalExp = finalExp.replaceAll("\\(~t\\)", "f");
        finalExp = finalExp.replaceAll("\\(~f\\)", "t");
        finalExp += '#';

        /*
            The basic thought of this algorithm is to create two stacks
            to store operators and booleans and continuously push operators
            or calculate according to level of top operator and current
            operator.

            For example, suppose the exp is "(t)&(~((f)|(t)))". After pretreatment,
            it will become "t&(~(f|t))#", the calculating steps are:
            1. A '#' is pushed into operators.
            2. 't' isn't a signal, stored with lastBool.
            3. lastBool is 't', true will be pushed into booleans. Current
               operator is '&', whose lever is higher than '#', pushed
               into operators.
            4. lastBool is 0. Current operator is '(', pushed into operators.
            5. lastBool is 0. Current operator is '~', pushed into operators.
            6. lastBool is 0. Current operator is '(', pushed into operators.
            7. 'f' isn't a signal, stored with lastBool.
            8. lastBool is 'f', false will be pushed into booleans. Current
               operator is '|', whose lever is higher than '(', pushed
               into operators.
            9. 't' isn't a signal, stored with lastBool.
            10.lastBool is 't', true will be pushed into booleans. Current
               operator is ')', whose level is lower than '|'. The true and
               false will be popped and calculate with '|'. Result is true, so
               true will be pushed into booleans. Continuously, compare ')'
               and '('. Their levels are all 0, so inner cycle will end.
            11. lastBool is 0. Current operator is ')', whose level is lower
                than '~'. The true will be popped and calculate with '~'.
                Result is false, so false will be pushed into booleans.
                Continuously, compare ')' with '('. Their levels are all 0,
                so inner cycle will end.
            12. lastBool is 0. Current operator is '#', whose level is lower
                than '&'. The false and true will be popped and calculate with
                '&'. Result is false, so false will be pushed into booleans.
                Continuously, compare '#' and '#'. There levels are all -1,
                inner cycle will end.
            13. All characters have been read. Final result is false.
         */

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
                        if (opr == '~') {
                            booleans.push(!booleans.pop());
                        } else {
                            boolean b2 = booleans.pop();
                            boolean b1 = booleans.pop();
                            booleans.push(calculate(b1, opr, b2));
                        }
                    } else if (cmp == 0) {
                        break;
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
