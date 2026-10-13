package com.bugbounty.dao;

import com.bugbounty.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RewardStatsDAO {

    public double getTotalReward(int userId) {
        String sql =
                "SELECT COALESCE(SUM(r.amount), 0) " +
                "FROM bug_rewards r " +
                "JOIN bug_reports b ON r.bug_id = b.bug_id " +
                "WHERE b.user_id = ?";

        return getAmount(sql, userId);
    }

    public double getPendingReward(int userId) {
        String sql =
                "SELECT COALESCE(SUM(r.amount), 0) " +
                "FROM bug_rewards r " +
                "JOIN bug_reports b ON r.bug_id = b.bug_id " +
                "WHERE b.user_id = ? " +
                "AND r.reward_status IN ('PENDING', 'APPROVED')";

        return getAmount(sql, userId);
    }

    public double getPaidReward(int userId) {
        String sql =
                "SELECT COALESCE(SUM(r.amount), 0) " +
                "FROM bug_rewards r " +
                "JOIN bug_reports b ON r.bug_id = b.bug_id " +
                "WHERE b.user_id = ? " +
                "AND r.reward_status = 'PAID'";

        return getAmount(sql, userId);
    }

    private double getAmount(String sql, int userId) {
        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}