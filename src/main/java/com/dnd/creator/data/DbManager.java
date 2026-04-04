package com.dnd.creator.data;
import java.sql.*;

public class DbManager {

    private static final String path = "jdbc:sqlite:src/main/data/data.db";

    public static void initialize(){

        try(Connection connection = DriverManager.getConnection(path);
            Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE IF NOT EXISTS races (id INTEGER PRIMARY KEY, name TEXT);");
            statement.execute("CREATE TABLE IF NOT EXISTS classes (id INTEGER PRIMARY KEY, name TEXT);");
            statement.execute("CREATE TABLE IF NOT EXISTS characters (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, race TEXT, class TEXT);");


        } catch (Exception _) {

        }

    }



}
