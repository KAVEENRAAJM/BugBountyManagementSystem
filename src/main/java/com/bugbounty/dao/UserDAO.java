package com.bugbounty.dao;

import com.bugbounty.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // =========================================
    // REGISTER USER
    // =========================================

    public boolean registerUser(
            String name,
            String email,
            String password) {

        String sql =
                "INSERT INTO users " +
                "(name, email, password, role) " +
                "VALUES (?, ?, ?, 'HACKER')";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            // Generate BCrypt password hash
            String hashedPassword =
                    BCrypt.hashpw(
                            password,
                            BCrypt.gensalt(12)
                    );

            statement.setString(
                    1,
                    name
            );

            statement.setString(
                    2,
                    email
            );

            statement.setString(
                    3,
                    hashedPassword
            );

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================
    // LOGIN USER
    // =========================================

    public String loginUser(
            String email,
            String password) {

        String sql =
                "SELECT password, role " +
                "FROM users " +
                "WHERE email = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    email
            );

            ResultSet resultSet =
                    statement.executeQuery();


            if (resultSet.next()) {

                String storedHash =
                        resultSet.getString(
                                "password"
                        );

                // Compare entered password
                // with BCrypt hash
                if (BCrypt.checkpw(
                        password,
                        storedHash)) {

                    return resultSet.getString(
                            "role"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }


    // =========================================
    // GET USER ID
    // =========================================

    public int getUserId(
            String email) {

        String sql =
                "SELECT user_id " +
                "FROM users " +
                "WHERE email = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    email
            );

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getInt(
                        "user_id"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return -1;
    }
}