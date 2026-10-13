package com.bugbounty.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:mariadb://localhost:3306/bug_bounty_db"
            );

    private static final String USER =
            System.getenv("DB_USER");

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection() {

        if (USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {
            System.err.println(
                    "Database configuration missing. Set DB_USER and DB_PASSWORD."
            );
            return null;
        }

        try {
            Class.forName("org.mariadb.jdbc.Driver");

            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed.");
            e.printStackTrace();
            return null;
        }
    }
}
