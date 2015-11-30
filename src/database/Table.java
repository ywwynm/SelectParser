package database;

import database.exceptions.FieldNotFoundException;
import utils.MatchUtils;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张启 on 2015/11/3.
 * Table in database.
 * A useful table should have {@link database.Table.Field}s and
 * {@link database.Table.Row}s.
 */
public class Table {

    private String mName;

    private List<Field> mFields;

    private List<Row> mRows = new ArrayList<>();

    public Table(String name) {
        mName = name;
    }

    public boolean setFields(String[] fieldNames, String[] fieldTypes) {
        if (mFields == null) {
            mFields = new ArrayList<>();
        } else {
            mFields.clear();
        }
        if (fieldNames == null || fieldTypes == null ||
                fieldNames.length != fieldTypes.length) {
            return false;
        }

        mRows.clear();
        for (int i = 0; i < fieldNames.length; i++) {
            Field field = new Field(fieldNames[i], fieldTypes[i]);
            mFields.add(field);
        }

        return true;
    }

    public String getName() {
        return mName;
    }

    public Field getFieldByName(String name)
            throws FieldNotFoundException {
        for (Field field : mFields) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        throw new FieldNotFoundException(name);
    }

    public List<Field> getFieldsByNames(List<String> names)
            throws FieldNotFoundException {
        List<Field> fields = new ArrayList<>();
        Field field;
        for (String name : names) {
            field = getFieldByName(name);
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

    public boolean addRow(LinkedHashMap<String, Object> rowData)
            throws FieldNotFoundException {
        int size = rowData.size();
        if (size != mFields.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            Field field = mFields.get(i);
            String fieldName = field.getName();
            Object value = rowData.get(fieldName);
            if (value == null) {
                throw new FieldNotFoundException(fieldName);
            }

            if (!MatchUtils.isValueMatchType(value, field.getType(),
                    rowData, fieldName)) {
                return false;
            }
        }
        mRows.add(new Row(rowData));
        return true;
    }

    class Field {

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

        @Override
        public String toString() {
            return "field.name:" + mName + ", field.type:" + mType;
        }
    }

    class Row {

        private LinkedHashMap<String, Object> mData;

        public Row(LinkedHashMap<String, Object> rowData) {
            mData = rowData;
        }

        /**
         * See if this row can match the requirement given by combination of parameters.
         * @param fieldName field's name.
         * @param opr operator signal. should be one of =,<,>,{,} and ^.
         * @param arg constant used to compare with.
         * @return -3 if one of parameters is empty;
         *          -2 if operator is invalid;
         *          -1 if field's type doesn't equal {@param arg}'s or they cannot
         *          compare with each other after legal type conversion;
         *          1 if this row can match the requirement, 0 otherwise.
         * @throws FieldNotFoundException if field with {@param fieldName} does not
         *          exist.
         */
        public int match(String fieldName, String opr, String arg)
                throws FieldNotFoundException {
            if (StringUtils.isEmptyString(fieldName)
                    || StringUtils.isEmptyString(opr)
                    || StringUtils.isEmptyString(arg)) {
                return -3;
            }

            if (!MatchUtils.isOperatorValid(opr)) {
                return -2;
            }

            Object value = mData.get(fieldName);
            if (value == null) {
                throw new FieldNotFoundException(fieldName);
            }

            Field field = getFieldByName(fieldName);
            return MatchUtils.isValueMatchComparison(value, opr, arg, field.getType());
        }

        public Object getValue(String columnName) throws FieldNotFoundException {
            Object value = mData.get(columnName);
            if (value == null) {
                throw new FieldNotFoundException(columnName);
            } else {
                return value;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Object o : mData.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue());
                sb.append(" ");
            }
            return sb.toString();
        }
    }
}
