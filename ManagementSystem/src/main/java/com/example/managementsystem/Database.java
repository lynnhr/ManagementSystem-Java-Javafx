package com.example.managementsystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database instance = null;
    private Connection conn = null;

    private Database() {}

    private void init() throws SQLException {
        final String DB_URL = "jdbc:mysql://localhost:3306/management";
        final String USER = "root";
        final String PASS = "say-my-name";

        conn = DriverManager.getConnection(DB_URL, USER, PASS);

        System.out.println("Connected to database");
    }

    public Connection getConnection() {
        return conn;
    }

    public static Database getInstance() throws SQLException {
        if (instance != null && !instance.getConnection().isClosed()) {
            return  instance;
        } else {
            instance = new Database();
            instance.init();
            return instance;
        }
    }
}
