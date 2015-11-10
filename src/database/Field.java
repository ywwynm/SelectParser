package database;

import java.util.List;

/**
 * Created by 张启 on 2015/11/3.
 * Field in database.
 */
public class Field {

    private String mName;
    private String mType;

    public Field(String name, String type) {
        mName = name;
        mType = type;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }
}
