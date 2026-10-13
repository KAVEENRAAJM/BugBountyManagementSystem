package com.bugbounty.dao;

import com.bugbounty.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RewardDAO {

    // =========================================
    // CREATE REWARD
    // =========================================

    public boolean createReward(
            int bugId,
            double amount,
            String rewardStatus) {

        String sql =
                "INSERT INTO bug_rewards " +
                "(bug_id, amount, reward_status) " +
                "VALUES (?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    bugId
            );

            statement.setDouble(
                    2,
                    amount
            );

            statement.setString(
                    3,
                    rewardStatus
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
    // UPDATE REWARD
    // =========================================

    public boolean updateReward(
            int bugId,
            double amount,
            String rewardStatus) {

        String sql =
                "UPDATE bug_rewards " +
                "SET amount = ?, " +
                "reward_status = ?, " +
                "awarded_at = CASE " +
                "WHEN ? = 'PAID' " +
                "THEN CURRENT_TIMESTAMP " +
                "ELSE NULL END " +
                "WHERE bug_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setDouble(
                    1,
                    amount
            );

            statement.setString(
                    2,
                    rewardStatus
            );

            statement.setString(
                    3,
                    rewardStatus
            );

            statement.setInt(
                    4,
                    bugId
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
    // CHECK IF REWARD EXISTS
    // =========================================

    public boolean rewardExists(
            int bugId) {

        String sql =
                "SELECT reward_id " +
                "FROM bug_rewards " +
                "WHERE bug_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    bugId
            );

            ResultSet resultSet =
                    statement.executeQuery();

            return resultSet.next();

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================
    // GET REWARD DETAILS
    // =========================================

    public String[] getReward(
            int bugId) {

        String sql =
                "SELECT reward_id, bug_id, " +
                "amount, reward_status, " +
                "awarded_at, created_at " +
                "FROM bug_rewards " +
                "WHERE bug_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    bugId
            );

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return new String[] {

                    String.valueOf(
                            resultSet.getInt(
                                    "reward_id"
                            )
                    ),

                    String.valueOf(
                            resultSet.getInt(
                                    "bug_id"
                            )
                    ),

                    String.valueOf(
                            resultSet.getDouble(
                                    "amount"
                            )
                    ),

                    resultSet.getString(
                            "reward_status"
                    ),

                    resultSet.getString(
                            "awarded_at"
                    ),

                    resultSet.getString(
                            "created_at"
                    )
                };
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}