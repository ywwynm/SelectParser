package database.exceptions;

/**
 * Created by 张启 on 2015/11/8.
 * Exception thrown when a column does not exist.
 */
public class FieldNotFoundException extends Exception {

    public FieldNotFoundException(String columnName) {
        super("Column " + "\"" + columnName + "\" does not exist");
    }
}
