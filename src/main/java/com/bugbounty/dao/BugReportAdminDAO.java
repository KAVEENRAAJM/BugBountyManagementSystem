package com.bugbounty.dao;

import com.bugbounty.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class BugReportAdminDAO {

    public boolean updateStatus(
            int bugId,
            String status) {

        String sql =
                "UPDATE bug_reports " +
                "SET status = ? " +
                "WHERE bug_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, status);
            statement.setInt(2, bugId);

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}