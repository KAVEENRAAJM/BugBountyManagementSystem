package com.bugbounty.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bugbounty.util.DBConnection;

public class AdminStatsDAO {

    public int getTotalReports() {
        return getCount(
                "SELECT COUNT(*) FROM bug_reports"
        );
    }

    public int getSubmittedReports() {
        return getCount(
                "SELECT COUNT(*) FROM bug_reports " +
                "WHERE status = 'SUBMITTED'"
        );
    }

    public int getUnderReviewReports() {
        return getCount(
                "SELECT COUNT(*) FROM bug_reports " +
                "WHERE status = 'UNDER REVIEW'"
        );
    }

    public int getVerifiedReports() {
        return getCount(
                "SELECT COUNT(*) FROM bug_reports " +
                "WHERE status = 'VERIFIED'"
        );
    }

    public int getResolvedReports() {
        return getCount(
                "SELECT COUNT(*) FROM bug_reports " +
                "WHERE status = 'RESOLVED'"
        );
    }

    public int getRejectedReports() {
        return getCount(
                "SELECT COUNT(*) FROM bug_reports " +
                "WHERE status = 'REJECTED'"
        );
    }

    private int getCount(String sql) {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}