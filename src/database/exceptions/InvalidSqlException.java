package database.exceptions;

/**
 * Created by 张启 on 2015/11/8.
 */
public class InvalidSqlException extends Exception {

    public InvalidSqlException() {
        super("Invalid sql sentence.");
    }

    public InvalidSqlException(String msg) {
        super(msg);
    }
}
