package database;

import core.LogicCalculator;
import core.ParamsSplitter;
import database.exceptions.InvalidSqlException;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张启 on 2015/11/8.
 * database
 */
public class Database {

    private static Database sDatabase;

    private List<Table> mTables;

    public static Database getInstance() {
        if (sDatabase == null) {
            synchronized (Database.class) {
                if (sDatabase == null) {
                    sDatabase = new Database();
                }
            }
        }
        return sDatabase;
    }

    public Database() {
        mTables = new ArrayList<>();
    }

    public void addTable(Table table) {
        if (table != null) {
            mTables.add(table);
        }
    }

    public List<String> execSQL(String sql) throws InvalidSqlException {
        ParamsSplitter splitter = ParamsSplitter.newInstance(sql);
        if (splitter == null) {
            throw new InvalidSqlException();
        }

        String select = splitter.getSelectedParams();
        String from   = splitter.getFromParams();
        String where  = splitter.getWhereParams();

        Table table = getTableByName(from);
        if (table == null) {
            return null;
        }

        List<Field> fields;
        if ("*".equals(select)) {
            fields = table.getFields();
        } else {
            fields = table.getFieldsByNames(splitter.splitSelectedParams());
        }
        if (fields == null) {
            return null;
        }
        String[] fieldNames = new String[fields.size()];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = fields.get(i).getName();
        }

        List<Row> matchedRows = new ArrayList<>();
        if (where.isEmpty()) {
            matchedRows = table.getRows();
        } else {
            //List<String> debug = new ArrayList<>();

            List<Row> rows = table.getRows();
            List<String> expInfos = splitter.splitWhereParams();
            String simplerWhere = splitter.getWhereParams();
            for (Row row : rows) {
                String simplerWhereForThisRow = simplerWhere;
                int offset = 0;
                for (String expInfo : expInfos) {
                    int colon = expInfo.lastIndexOf(":");
                    String exp = expInfo.substring(0, colon);
                    int expLength = exp.length();
                    exp = exp.replaceAll(" ", "");

                    int expInfoLength = expInfo.length();
                    int index = Integer.valueOf(expInfo
                            .substring(colon + 1, expInfoLength));
                    index -= offset;

                    String[] binaryExp = splitter.splitBinaryExpression(exp);
                    boolean match = row.match(
                            binaryExp[0], binaryExp[1], binaryExp[2]);
                    String replacement = match ? "t" : "f";
                    simplerWhereForThisRow = StringUtils.replaceBetweenIndex(
                            simplerWhereForThisRow, replacement,
                            index, index + expLength);
                    offset = offset + expLength - 1;
                }
                if (LogicCalculator.calculate(
                        simplerWhereForThisRow.replaceAll(" ", ""))) {
                    matchedRows.add(row);
                }
                //debug.add(simplerWhereForThisRow.replaceAll(" ", ""));
            }

            //debug.forEach(System.out::println);
        }

        List<String> result = new ArrayList<>();
        for (Row matchedRow : matchedRows) {
            HashMap<String, Object> columnsAndValues =
                    matchedRow.getColumnsAndValues(fieldNames);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry entry : columnsAndValues.entrySet()) {
                sb.append(entry.getKey()).append(":")
                        .append(entry.getValue()).append(" ");
            }
            result.add(sb.toString());
        }
        return result;
    }

    public Table getTableByName(String name) {
        for (Table table : mTables) {
            if (name.equals(table.getName())) {
                return table;
            }
        }
        return null;
    }
}
