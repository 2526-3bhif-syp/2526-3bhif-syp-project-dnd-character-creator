package com.dnd.creator.data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbManager {

    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:src/main/data/data.db";

    public void connect(){

        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection with Database successful!");
        } catch (SQLException e) {

            System.out.println("Connection with Database failed!");
        }
    }

    public List<String> getAllRaces(){

        List<String> result = new ArrayList<>();

        String query= "SELECT name FROM races ORDER BY name";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)){

            while(resultSet.next()){

                result.add(resultSet.getString("name"));

            }


        } catch (SQLException e) {

            System.err.println(e.getMessage());

        }

        return result;
    }




}
