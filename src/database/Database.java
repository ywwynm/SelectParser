package database;

import core.LogicCalculator;
import core.ParamsSplitter;
import database.exceptions.InvalidSqlException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张启 on 2015/11/8.
 * A simulation for a scheme in database.
 * Tables will be added to this so that expression after "from" will
 * be meaningful.
 */
public class Database {

    private List<Table> mTables;

    public Database() {
        mTables = new ArrayList<>();
    }

    public boolean addTable(Table table) {
        if (table == null) {
            return false;
        }
        for (Table t : mTables) {
            if (table.equals(t)) {
                return false;
            }
        }
        mTables.add(table);
        return true;
    }

    /**
     * Query the database and find rows that match user's requirements
     * and with given fields from each table.
     * @param sql the select sql to execute
     * @return matched rows with given fields described by words.
     * @throws InvalidSqlException if {@param sql} is invalid to parse
     * and execute.
     */
    public List<String> query(String sql) throws InvalidSqlException {
        ParamsSplitter splitter = ParamsSplitter.newInstance(sql);
        if (splitter == null) {
            throw new InvalidSqlException();
        }

        String select = splitter.getSelectParams();
        String from   = splitter.getFromParams();
        String where  = splitter.getWhereParams();

        Table table = getTableByName(from);
        if (table == null) {
            throw new InvalidSqlException(
                    "No table matched in this database");
        }

        List<Field> fields;
        if ("*".equals(select)) {
            fields = table.getFields();
        } else {
            fields = table.getFieldsByNames(splitter.splitSelectParams());
        }
        if (fields == null) {
            throw new InvalidSqlException(
                    "Wrong column names for this table");
        }

        String[] fieldNames = new String[fields.size()];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = fields.get(i).getName();
        }

        List<Row> matchedRows = new ArrayList<>();
        if (where.isEmpty()) {
            matchedRows = table.getRows();
        } else {
            List<Row> rows = table.getRows();

            // Atomic expressions with their indexes after "where".
            List<String> expInfos = splitter.splitWhereParams();

            // The expression after "where" with logical operator in words
            // replaced with signals.
            String simplerWhere = splitter.getWhereParams();

            /*
                The basic thought of this algorithm is to replace every
                atomic expressions after "where" with their values for each
                row at first. Then handle logical calculates with a Stack.

                For example, if expression after "where" is
                "((a<3) and (not (b>5)))", after pretreatment by ParamsSplitter,
                it will become "(a<3)&(!(b>5))". And the expInfos should
                be { "a<3:1", "b>5:9" }. For each row, we can get if its "a"
                is less than 3 and "b" is greater than 5 so that we can replace
                them with values for original expression like "(t)&(!(f))".
                Then we pass this to LogicCalculator.calculate() and get the
                final boolean result. If it's true, we will add this row into
                matchedRows.
             */
            for (Row row : rows) {
                String simplerWhereForThisRow = simplerWhere;

                /*
                    After replacing an atomic expression with its value for
                    given row, the index of next expression will change. We
                    need to track this change.
                 */
                int offset = 0;
                for (String expInfo : expInfos) {
                    // Get the atomic expression
                    int colon = expInfo.lastIndexOf(":");
                    String exp = expInfo.substring(0, colon);
                    int expLength = exp.length();

                    // Get index of atomic expression in original
                    // expression(simplerWhere)
                    int expInfoLength = expInfo.length();
                    int index = Integer.valueOf(expInfo
                            .substring(colon + 1, expInfoLength));
                    index -= offset;

                    // replace atomic expression with its value for this row
                    String[] binaryExp = splitter.splitBinaryExpression(exp);
                    int match = row.match(
                            binaryExp[0], binaryExp[1], binaryExp[2]);
                    if (match == -2) {
                        throw new InvalidSqlException(
                                "Wrong column names for this table");
                    } else if (match == -1) {
                        throw new InvalidSqlException(
                                "fields' types do not always equal constants' types");
                    }
                    String replacement = match == 1 ? "t" : "f";
                    simplerWhereForThisRow = replaceBetweenIndex(
                            simplerWhereForThisRow, replacement,
                            index, index + expLength);

                    offset = offset + expLength - 1;
                }
                if (LogicCalculator.calculate(simplerWhereForThisRow)) {
                    matchedRows.add(row);
                }
            }
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

    private static String replaceBetweenIndex(String src, String replacement,
                                             int from, int end) {
        return src.substring(0, from) + replacement + src.substring(end, src.length());
    }
}
