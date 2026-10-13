package com.bugbounty.dao;

import com.bugbounty.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminRewardStatsDAO {

    public double getTotalBounty() {

        String sql =
                "SELECT COALESCE(SUM(amount), 0) " +
                "FROM bug_rewards";

        return getAmount(sql);
    }


    public double getPendingBounty() {

        String sql =
                "SELECT COALESCE(SUM(amount), 0) " +
                "FROM bug_rewards " +
                "WHERE reward_status IN ('PENDING', 'APPROVED')";

        return getAmount(sql);
    }


    public double getPaidBounty() {

        String sql =
                "SELECT COALESCE(SUM(amount), 0) " +
                "FROM bug_rewards " +
                "WHERE reward_status = 'PAID'";

        return getAmount(sql);
    }


    private double getAmount(String sql) {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}