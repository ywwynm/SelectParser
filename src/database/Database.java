package database;

import core.LogicCalculator;
import core.ParamsSplitter;
import database.exceptions.FieldNotFoundException;
import database.exceptions.InvalidSqlException;
import javafx.util.Pair;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
     * @param sql the "select" sql to parse and execute
     * @return matched rows with given fields described in words.
     * @throws InvalidSqlException if {@param sql} is invalid to parse
     *          and execute.
     */
    public List<String> query(String sql) throws
            InvalidSqlException, FieldNotFoundException {
        if (StringUtils.isEmptyString(sql)) {
            throw new InvalidSqlException("SQL statement is empty");
        }
        ParamsSplitter splitter = new ParamsSplitter(sql);
        String from = splitter.getFromParams();

        if (splitter.getSelectParams().isEmpty() || from.isEmpty()) {
            throw new InvalidSqlException();
        }

        Table table = getTableByName(from);
        if (table == null) {
            throw new InvalidSqlException("Table " + from + " does not exist " +
                    "in this database");
        }

        String[] fieldNames = getFieldNamesForSelect(table, splitter);

        List<Table.Row> matchedRows;
        if (splitter.getWhereParams().isEmpty()) {
            matchedRows = table.getRows();
        } else {
            matchedRows = queryForExps(table, splitter);
        }

        return getQueryResultDescriptions(matchedRows, fieldNames);
    }

    public Table getTableByName(String name) {
        for (Table table : mTables) {
            if (name.equals(table.getName())) {
                return table;
            }
        }
        return null;
    }

    private List<Table.Field> getFieldsForSelect(Table table,
                                                 ParamsSplitter splitter)
            throws FieldNotFoundException {
        String select = splitter.getSelectParams();
        if ("*".equals(select)) {
            return table.getFields();
        } else {
            return table.getFieldsByNames(splitter.splitSelectParams());
        }
    }

    private String[] getFieldNamesForSelect(Table table,
                                            ParamsSplitter splitter)
            throws FieldNotFoundException {
        List<Table.Field> fields = getFieldsForSelect(table, splitter);
        String[] fieldNames = new String[fields.size()];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = fields.get(i).getName();
        }
        return fieldNames;
    }

    private List<Table.Row> queryForExps(Table table, ParamsSplitter splitter)
            throws InvalidSqlException, FieldNotFoundException {
        List<Table.Row> rows = table.getRows();
        List<Table.Row> matchedRows = new ArrayList<>();

        // Atomic expressions with their indexes after "where".
        List<String> expInfos = splitter.splitWhereParams();

        // The expression after "where" with logical operator in words
        // replaced with signals.
        String where = splitter.getWhereParams();

            /*
                The basic thought of this algorithm is to replace every
                atomic expressions after "where" with their values for each
                row at first. Then handle logical calculates using a Stack.

                For example, if expression after "where" is
                "((a<3) and (not (b>5)))", after pretreatment by ParamsSplitter,
                it will become "(a<3)&(~(b>5))". And the expInfos should
                be { "a<3:1", "b>5:9" }. For each row, we can get if its "a"
                is less than 3 and "b" is greater than 5 so that we can replace
                them with values for original expression like "(t)&(~(f))".
                Then we pass this to LogicCalculator.calculate() and get the
                final boolean result. If it's true, we will add this row into
                matchedRows.
             */
        for (Table.Row row : rows) {
            if (isRowMatchExp(row, expInfos, where, splitter)) {
                matchedRows.add(row);
            }
        }
        return matchedRows;
    }

    private boolean isRowMatchExp(Table.Row row, List<String> expInfos,
                                  String where, ParamsSplitter splitter)
            throws FieldNotFoundException, InvalidSqlException {
        // The expression after "where" with logical operator in words
        // replaced with signals.
        String whereForThisRow = where;

        /*
           After replacing an atomic expression with its value for
           given row, the index of next expression will change. We
           need to track this change.
        */
        int offset = 0;
        for (String expInfo : expInfos) {
            Pair<String, Integer> pair = replaceExpWithValue(row, expInfo,
                    offset, splitter, whereForThisRow);
            whereForThisRow = pair.getKey();
            offset = pair.getValue();
        }
        return LogicCalculator.calculate(whereForThisRow);
    }

    private Pair<String, Integer> replaceExpWithValue(Table.Row row, String expInfo, int offset,
                                                      ParamsSplitter splitter, String where)
            throws InvalidSqlException, FieldNotFoundException {
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

        String replacement = getReplacementForExp(row, exp, splitter);

        String updatedWhere = StringUtils.replaceSubstring(
                where, replacement,
                index, index + expLength);
        int updatedOffset = offset + expLength - 1;
        return new Pair<>(updatedWhere, updatedOffset);
    }

    private String getReplacementForExp(Table.Row row, String exp, ParamsSplitter splitter)
            throws FieldNotFoundException, InvalidSqlException {
        // replace atomic expression with its value for this row
        String[] binaryExp = splitter.splitBinaryExpression(exp);

        int match = row.match(
                binaryExp[0], binaryExp[1], binaryExp[2]);
        if (match == -2) {
            throw new InvalidSqlException(binaryExp[1] + " is not valid " +
                    "operator");
        } else if (match == -1) {
            throw new InvalidSqlException(binaryExp[0] + "'s type cannot be " +
                    "compared with argument's");
        }
        return match == 1 ? "t" : "f";
    }

    private List<String> getQueryResultDescriptions(List<Table.Row> rows,
                                                    String[] fieldNames)
            throws FieldNotFoundException {
        List<String> descriptions = new ArrayList<>();
        for (Table.Row row : rows) {
            StringBuilder sb = new StringBuilder();
            for (String fieldName : fieldNames) {
                Object value = row.getValue(fieldName);
                sb.append(fieldName).append(":").append(value).append(" ");
            }
            descriptions.add(sb.toString());
        }
        return descriptions;
    }
}
