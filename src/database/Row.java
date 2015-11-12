package database;

import database.exceptions.FieldNotExistException;
import utils.MatchUtils;

import java.util.*;

/**
 * Created by 张启 on 2015/11/3.
 * Row in a table.
 */
public class Row {

    private Table table;

    private LinkedHashMap<String, Object> mValues;

    public Row() {
        mValues = new LinkedHashMap<>();
    }

    public Row(LinkedHashMap<String, Object> values) {
        mValues = values;
    }

    void setTable(Table table) {
        this.table = table;
    }

    public int match(String columnName, String opr, String arg) {
        Object value = mValues.get(columnName);
        if (value == null) {
            return -2;
        }

        Field field = table.getFieldByName(columnName);
        String type = field.getType();

        if ("integer".equals(type)) {
            Integer argInt;
            try {
                argInt = Integer.valueOf(arg);
            } catch (NumberFormatException e) {
                Double argDbl;
                try {
                    argDbl = Double.valueOf(arg);
                } catch (NumberFormatException doubleE) {
                    return -1;
                }
                return MatchUtils.match((Integer) value, opr, argDbl) ? 1 : 0;
            }
            return MatchUtils.match((Integer) value, opr, argInt) ? 1 : 0;
        } else if ("double".equals(type) || "integer".equals(type)) {
            Double argDbl;
            try {
                argDbl = Double.valueOf(arg);
            } catch (NumberFormatException e) {
                return -1;
            }
            return MatchUtils.match((Double) value, opr, argDbl) ? 1 : 0;
        } else if ("varchar".equals(type)) {
            if (!arg.startsWith("'") || !arg.endsWith("'")) {
                return -1;
            } else {
                return MatchUtils.match((String) value, opr,
                        arg.replaceAll("'", "")) ? 1 : 0;
            }
        }
        return 0;
    }

    public List<String> getFieldNames() {
        List<String> keys = new ArrayList<>(mValues.size());
        for (Map.Entry entry : mValues.entrySet()) {
            keys.add((String) entry.getKey());
        }
        return keys;
    }

    public Object getValue(String columnName) throws FieldNotExistException {
        Object value = mValues.get(columnName);
        if (value == null) {
            throw new FieldNotExistException();
        } else {
            return value;
        }
    }

    public List<Object> getValues(String... columnNames)
            throws FieldNotExistException {
        List<Object> values = new ArrayList<>(columnNames.length);
        Object value;
        for (String columnName : columnNames) {
            value = mValues.get(columnName);
            if (value == null) {
                throw new FieldNotExistException();
            } else {
                values.add(value);
            }
        }
        return values;
    }

    public List<Object> getValues() {
        List<Object> values = new ArrayList<>(mValues.size());
        for (Map.Entry entry : mValues.entrySet()) {
            values.add(entry.getValue());
        }
        return values;
    }

    public LinkedHashMap<String, Object> getColumnsAndValues() {
        return mValues;
    }

    public LinkedHashMap<String, Object> getColumnsAndValues(String... columnNames) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (String columnName : columnNames) {
            result.put(columnName, mValues.get(columnName));
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object o : mValues.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            sb.append(" ");
        }
        return sb.toString();
    }
}
