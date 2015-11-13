import database.Database;
import database.Table;
import database.exceptions.FieldNotFoundException;

import java.util.*;

/**
 * Created by 张启 on 2015/11/3.
 * Test database and "select" SQL.
 */
public class Test {

    public static void main(String... args) {
        Database database = new Database();
        database.addTable(generateTestTable1());
        database.addTable(generateTestTable2());

        List<String> result = null;
        try {
            long start = System.currentTimeMillis();

//            result = database.query("select (*) from (user)");

//            result = database.query("select (name) from (user) " +
//                    "where (height >= 176)");
//            result = database.query("select (*) from (user) " +
//                    "where (age<>20.000000)");
//            result = database.query("select (name, sex, height) from (user) " +
//                    "where (((age=19) or (age=20)) and (not ((height>=175) or (weight<=60))))");
//            result = database.query("select (*) from (user) " +
//                    "where (((age > 19) and (sex='男')) or ((height<174) and (weight>=57)))");

//            result = database.query("select (name) from (user) " +
//                    "where (((not (not (name='zq')))) or (not (name<>'zq')))");

//            result = database.query("select (selectFrom) from (strange_table) " +
//                    "where (whereFrom >='b')");
            result = database.query("select (whereFrom) from (strange_table)" +
                    "where (hehe='lala')");

            long end = System.currentTimeMillis();

            if (result != null) {
                System.out.println("result:");
                result.forEach(System.out::println);
                System.out.println("It costs " + (end - start) + "ms to query.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Table generateTestTable1() {
        String[] fieldNames = { "name", "sex", "age", "height", "weight" };
        String[] fieldTypes = { "varchar", "varchar", "integer", "integer", "double" };

        Table user = new Table("user");
        user.setFields(fieldNames, fieldTypes);

        Object[][] datas = new Object[5][];
        datas[0] = new String[]  { "'zq'", "'cjq'", "'zqm'", "'cyx'", "'yxh'", "'myf'" };
        datas[1] = new String[]  { "'男'", "'女'", "'男'", "'女'", "'女'", "'男'" };
        datas[2] = new Integer[] { 19, 20, 20, 20, 20, 20 };
        datas[3] = new Integer[] { 169, 178, 174, 170, 172, 172 };
        datas[4] = new Double[]  { 54.0, 60.0, 60.4, 56.0, 58.7, 72.9 };
        LinkedHashMap<String, Object> rowData;
        for (int i = 0; i < datas[0].length; i++) {
            rowData = new LinkedHashMap<>(datas.length);
            for (int j = 0; j < datas.length; j++) {
                rowData.put(fieldNames[j], datas[j][i]);
            }
            try {
                user.addRow(rowData);
            } catch (FieldNotFoundException e) {
                e.printStackTrace();
            }
        }
        rowData = new LinkedHashMap<>(5);
        rowData.put("name", "'qhw'");
        rowData.put("sex", "'女'");
        rowData.put("age", 20);
        rowData.put("height", 168);
        rowData.put("weight", 54);
        try {
            user.addRow(rowData);
        } catch (FieldNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    private static Table generateTestTable2() {
        String[] fieldNames = { "selectFrom", "whereFrom" };
        String[] fieldTypes = { "varchar", "varchar" };

        Table strange = new Table("strange_table");
        strange.setFields(fieldNames, fieldTypes);

        Object[][] datas = new Object[2][];
        datas[0] = new String[]  { "'hehe'", "'memeda'", "'mengmengda'" };
        datas[1] = new String[]  { "'alie'", "'bikaqiu'", "'enne'" };
        LinkedHashMap<String, Object> rowData;
        for (int i = 0; i < datas[0].length; i++) {
            rowData = new LinkedHashMap<>(datas.length);
            for (int j = 0; j < datas.length; j++) {
                rowData.put(fieldNames[j], datas[j][i]);
            }
            try {
                strange.addRow(rowData);
            } catch (FieldNotFoundException e) {
                e.printStackTrace();
            }
        }
        return strange;
    }

}
