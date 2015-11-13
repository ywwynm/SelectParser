package database.exceptions;

/**
 * Created by 张启 on 2015/11/8.
 * Exception thrown when SQL statement is invalid.
 */
public class InvalidSqlException extends Exception {

    public InvalidSqlException() {
        super("Invalid sql statement");
    }

    public InvalidSqlException(String msg) {
        super(msg);
    }
}
