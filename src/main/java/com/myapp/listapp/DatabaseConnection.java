package com.myapp.listapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public Connection databaseLink;

    public Connection getDBConnection() {

        String url = "jdbc:sqlite:/Users/Jan4iks/Desktop/Skola/DB/ListDB.db";

        try {
            databaseLink = DriverManager.getConnection(url);
            System.out.println("Database operation successful");

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return databaseLink;
    }
}
