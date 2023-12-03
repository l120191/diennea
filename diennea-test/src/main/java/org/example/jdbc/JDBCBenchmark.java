package org.example.jdbc;

import org.example.property.PropertyManager;

import java.io.IOException;
import java.sql.*;
import java.util.Base64;

public class JDBCBenchmark {

    private static final String tableName = "Users";
    private static final String primaryKeyColumnName = "user_id";
    private static Connection conn;
    private static JDBCBenchmark instance;


    public static JDBCBenchmark getInstance() throws IOException {
        if (instance == null){
            instance = new JDBCBenchmark();
            setConnection();
        }
        return instance;
    }

    public static void setConnection(){
        try{
            String pwdDecoded = new String(Base64.getDecoder().decode(
                    PropertyManager.getInstance().getPropertyByName("jdbc.pswd")));

            conn = DriverManager.getConnection(
                    PropertyManager.getInstance().getPropertyByName("jdbc.url"),
                    PropertyManager.getInstance().getPropertyByName("jdbc.user"),
                    pwdDecoded);

            conn.setAutoCommit(false);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createTable(String query) throws SQLException {
        PreparedStatement ps;
        boolean isExist = true;
        try {
            ps = conn.prepareStatement("SELECT * FROM " + tableName);
            ps.execute();
        } catch (Exception e) {
            if (e instanceof SQLSyntaxErrorException)
                isExist = false;
        }

        if (!isExist) {
            ps = conn.prepareStatement(query);
            ps.execute();
            conn.commit();
        }
    }

    public void runInsertBenchmarks(String [] insertQuery) throws SQLException {
        long totalInsertTime = 0;
        long minInsertTime = Long.MAX_VALUE;
        long maxInsertTime = 0;
        int numberOfIterations = 10; // X statements

        for (int i = 1; i < insertQuery.length; i++) {
            long startTime = System.currentTimeMillis();

            // Perform the INSERT statement
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery[i])) {
                preparedStatement.executeUpdate();
            }
            if(i%numberOfIterations==0)
                conn.commit();

            long endTime = System.currentTimeMillis();
            long insertTime = endTime - startTime;

            totalInsertTime += insertTime;
            minInsertTime = Math.min(minInsertTime, insertTime);
            maxInsertTime = Math.max(maxInsertTime, insertTime);
        }

        printBenchmarkResults("INSERT", minInsertTime, maxInsertTime, totalInsertTime / insertQuery.length);
    }

    public void runSelectBenchmarks() throws SQLException {
        long totalSelectTime = 0;
        long minSelectTime = Long.MAX_VALUE;
        long maxSelectTime = 0;
        int numberOfIterations = 50; // Adjust the number of iterations as needed

        for (int i = 0; i < numberOfIterations; i++) {
            long startTime = System.currentTimeMillis();

            try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE " + primaryKeyColumnName + " = ?")) {
                preparedStatement.setInt(1, i); // Assuming the primary key is an integer ID
                ResultSet resultSet = preparedStatement.executeQuery();  // resultset unused
            }

            long endTime = System.currentTimeMillis();
            long selectTime = endTime - startTime;

            totalSelectTime += selectTime;
            minSelectTime = Math.min(minSelectTime, selectTime);
            maxSelectTime = Math.max(maxSelectTime, selectTime);
        }

        printBenchmarkResults("SELECT", minSelectTime, maxSelectTime, totalSelectTime / numberOfIterations);
    }

    private static void printBenchmarkResults(String operation, long minTime, long maxTime, long avgTime) {
        System.out.println(operation + " benchmarks:");
        System.out.println("  Min Time: " + minTime + " ms");
        System.out.println("  Max Time: " + maxTime + " ms");
        System.out.println("  Avg Time: " + avgTime + " ms\n");
    }

    public void closeConnecion() throws SQLException {
        conn.close();
    }

}