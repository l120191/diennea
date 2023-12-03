package org.example;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import org.example.jdbc.JDBCBenchmark;


public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        JDBCBenchmark jdbcBenchmark = JDBCBenchmark.getInstance();
        String sqlSchemaTable = "schema/schema.sql";
        String sqlInsertData = "schema/data.sql";

        if(jdbcBenchmark!=null){

            // creation of the table
            String queryCreateTable = retrieveSQLFileContent(sqlSchemaTable);
            jdbcBenchmark.createTable(queryCreateTable);

            String[] queryInsert = retrieveSQLFileContentArr(sqlInsertData);
            jdbcBenchmark.runInsertBenchmarks(queryInsert);
            jdbcBenchmark.runSelectBenchmarks();
            jdbcBenchmark.closeConnecion();
        }
    }

    private static String retrieveSQLFileContent(String sqlFilePath) throws IOException {
        String result = "";
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(sqlFilePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        if (bufferedReader != null) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
        } else {
            System.out.println("inputStream retrieveSQLFileContent ERROR");
        }

        return result;
    }

    private static String[] retrieveSQLFileContentArr(String sqlFilePath) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(sqlFilePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        if (bufferedReader != null) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
        } else {
            System.out.println("inputStream retrieveSQLFileContent ERROR");
        }

        return result.toArray(new String[result.size()]);
    }

}