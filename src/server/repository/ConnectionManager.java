package server.repository;

import server.questions.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    // jeśli nie uda się połączyć z bazą trzeba wejśc w ten plik SQLServerManager14.msc

    private final String tab = "\t\t";

    public ConnectionManager() throws SQLException, ClassNotFoundException { }

    private Connection getConnection() throws ClassNotFoundException, SQLException {

        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String datebaseName = "Milionerzy";
        String url = "jdbc:sqlserver://DESKTOP-1ODQS4E\\SQLEXPRESS;databaseName=Milionerzy;integratedSecurity=true;";

        Connection connection = null;
        try{
            Class.forName(driverName);
            connection = DriverManager.getConnection(url);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        System.out.println("Connected to datebase");
        return connection;
    }

    private Connection connection = getConnection();

    private ResultSet getResult(String sqlQuery) throws SQLException, ClassNotFoundException {
        return connection.createStatement().executeQuery(sqlQuery);
    }

    public void showAllQuestions() throws SQLException, ClassNotFoundException {

        String sqlQuery = "select * from Pytania";
        ResultSet resultSet = getResult(sqlQuery);
        ResultSetMetaData metaData = resultSet.getMetaData();

        List<String> columns = new ArrayList<>();
        int numberOfColumns = metaData.getColumnCount();

        System.out.println("Number of columns " + numberOfColumns + "\n");

        for(int i=1; i<=numberOfColumns; ++i) {
            columns.add(metaData.getColumnName(i));
        }
        String columnsNames = "";
        for(String s : columns) {
            columnsNames += s + tab + tab;
        }
        System.out.println(columnsNames + "---");

        String rowInfo = "";
        int temp = 0;

        while (resultSet.next()) {
            for(String s : columns) {
                if (temp == numberOfColumns-1) {
                    rowInfo += resultSet.getString(s) + tab;
                    System.out.println(rowInfo);
                    rowInfo = "";
                    temp = 0;
                } else {
                    rowInfo += resultSet.getString(s) + tab;
                    temp++;
                }
            }
        }
    }

    public Question getQuestionByID(int id) throws SQLException, ClassNotFoundException {

        Question question = new Question();
        String sqlQuery = "SELECT * FROM Pytania WHERE idPytania = " + id;

        ResultSet resultSet = getResult(sqlQuery);
        ResultSetMetaData metaData = resultSet.getMetaData();

        List<String> columns = new ArrayList<>();
        int numberOfColumns = metaData.getColumnCount();

//        System.out.println("Number of columns " + numberOfColumns + "\n");

        for(int i=1; i<=numberOfColumns; ++i) {
            columns.add(metaData.getColumnName(i));
        }

        String columnsNames = "";
        for(String s : columns) {
            columnsNames += s + tab;
        }
        //System.out.println(columnsNames + "---");

        while(resultSet.next()) {
            for(String s : columns) {
                question.addToList(resultSet.getString(s));
//                System.out.println(" wynik = " + resultSet.getString(s));
            }
        }
        question.setAllData();
        return question;
    }
}