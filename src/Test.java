import database.Database;
import database.Field;
import database.Row;
import database.Table;
import database.exceptions.InvalidSqlException;

import java.util.*;

/**
 * Created by 张启 on 2015/11/3.
 * Test class for
 */
public class Test {

    public static void main(String... args) {
        String[] fieldNames = { "name", "sex", "age", "height", "weight" };
        String[] fieldTypes = { "varchar", "varchar", "integer", "integer", "double" };

        List<Field> fields = new ArrayList<>();
        for (int i = 0; i < fieldNames.length; i++) {
            fields.add(new Field(fieldNames[i], fieldTypes[i]));
        }

        Table user = new Table("user", fields);

        Object[][] datas = new Object[5][];
        datas[0] = new String[]  { "zq", "cjq", "zqm", "cyx", "yxh", "myf" };
        datas[1] = new String[]  { "男", "女", "男", "女", "女", "男" };
        datas[2] = new Integer[] { 19, 20, 20, 20, 20, 20 };
        datas[3] = new Integer[] { 169, 178, 174, 170, 172, 172 };
        datas[4] = new Double[]  { 54.0, 60.0, 60.4, 56.0, 58.7, 72.9 };
        LinkedHashMap<String, Object> rowData;
        for (int i = 0; i < datas[0].length; i++) {
            rowData = new LinkedHashMap<>(datas.length);
            for (int j = 0; j < datas.length; j++) {
                rowData.put(fieldNames[j], datas[j][i]);
            }
            user.addRow(new Row(rowData));
        }

        Database database = new Database();
        database.addTable(user);

        List<String> result;
        try {
//            result = database.query("select (name, sex, height) from (user) " +
//                    "where (((age=19) or (age=20)) and (not ((height>=175) or (weight<=60))))");
//            result = database.query("select (*) from (user) " +
//                    "where (((age > 19) and (sex='男')) or ((height<174) and (weight>=57)))");
            result = database.query("select (name) from (user) " +
                    "where (((not (not (name='zq')))) or (not (name<>'zq')))");
//            result = database.query("select (name) from (user) " +
//                    "where (height >= 176)");
            System.out.println("result:");
            for (String row: result) {
                System.out.println(row);
            }
        } catch (InvalidSqlException e) {
            e.printStackTrace();
        }
    }

}
