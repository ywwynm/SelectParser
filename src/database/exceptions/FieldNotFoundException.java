package database.exceptions;

/**
 * Created by 张启 on 2015/11/8.
 */
public class FieldNotFoundException extends Exception {

    public FieldNotFoundException() {
        super("Column does not exist");
    }

    public FieldNotFoundException(String columnName) {
        super("Column " + "\"" + columnName + "\" does not exist");
    }
}
