package database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张启 on 2015/11/3.
 * Table in database.
 */
public class Table {

    private String mName;

    private List<Field> mFields;

    private List<Row> mRows;

    public Table(String name, List<Field> fields) {
        mName   = name;
        mFields = fields;
        mRows   = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public Field getFieldByName(String name) {
        for (Field field : mFields) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    public List<Field> getFieldsByNames(List<String> names) {
        List<Field> fields = new ArrayList<>();
        Field field;
        for (String name : names) {
            field = getFieldByName(name);
            if (field == null) {
                return null;
            }
            fields.add(field);
        }
        return fields;
    }

    public List<Field> getFields() {
        return mFields;
    }

    public List<Row> getRows() {
        return mRows;
    }

    public boolean addRow(Row row) {
        List<String> fieldNames = row.getFieldNames();
        int size = fieldNames.size();
        if (size != mFields.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!mFields.get(i).getName().equals(fieldNames.get(i))) {
                return false;
            }
        }
        row.setTable(this);
        mRows.add(row);
        return true;
    }
}
