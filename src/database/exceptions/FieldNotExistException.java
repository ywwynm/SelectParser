package database.exceptions;

/**
 * Created by 张启 on 2015/11/8.
 */
public class FieldNotExistException extends Exception {

    public FieldNotExistException() {
        super("Column does not exist.");
    }
}
