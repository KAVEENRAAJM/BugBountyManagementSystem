package com.bugbounty.dao;

import com.bugbounty.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BugReportDAO {

    // =========================================
    // SUBMIT BUG
    // =========================================

    public boolean submitBug(
            int userId,
            String title,
            String vulnerabilityType,
            String description,
            String severity,
            String affectedUrl,
            String stepsToReproduce,
            String evidence) {

        String sql =
                "INSERT INTO bug_reports " +
                "(user_id, title, vulnerability_type, description, " +
                "severity, affected_url, steps_to_reproduce, evidence) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            statement.setString(2, title);
            statement.setString(3, vulnerabilityType);
            statement.setString(4, description);
            statement.setString(5, severity);
            statement.setString(6, affectedUrl);
            statement.setString(7, stepsToReproduce);
            statement.setString(8, evidence);

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================
    // GET HACKER REPORTS + REWARD
    // =========================================

    public List<String[]> getBugReportsByUserId(
            int userId) {

        List<String[]> reports =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "b.bug_id, " +
                "b.title, " +
                "b.vulnerability_type, " +
                "b.severity, " +
                "b.status, " +
                "b.submitted_at, " +
                "COALESCE(r.amount, 0) AS reward_amount, " +
                "COALESCE(r.reward_status, 'PENDING') AS reward_status " +
                "FROM bug_reports b " +
                "LEFT JOIN bug_rewards r " +
                "ON b.bug_id = r.bug_id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.submitted_at DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );

            ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                String[] report = {

                    // 0 - Bug ID
                    String.valueOf(
                            resultSet.getInt(
                                    "bug_id"
                            )
                    ),

                    // 1 - Title
                    resultSet.getString(
                            "title"
                    ),

                    // 2 - Vulnerability Type
                    resultSet.getString(
                            "vulnerability_type"
                    ),

                    // 3 - Severity
                    resultSet.getString(
                            "severity"
                    ),

                    // 4 - Bug Status
                    resultSet.getString(
                            "status"
                    ),

                    // 5 - Submitted At
                    resultSet.getString(
                            "submitted_at"
                    ),

                    // 6 - Reward Amount
                    String.valueOf(
                            resultSet.getDouble(
                                    "reward_amount"
                            )
                    ),

                    // 7 - Reward Status
                    resultSet.getString(
                            "reward_status"
                    )
                };

                reports.add(report);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return reports;
    }


    // =========================================
    // GET HACKER BUG DETAILS
    // =========================================

    public String[] getBugDetailsByUserId(
            int bugId,
            int userId) {

        String sql =
                "SELECT " +
                "b.bug_id, " +
                "b.title, " +
                "b.vulnerability_type, " +
                "b.description, " +
                "b.severity, " +
                "b.affected_url, " +
                "b.steps_to_reproduce, " +
                "b.evidence, " +
                "b.status, " +
                "b.submitted_at, " +
                "COALESCE(r.amount, 0) AS reward_amount, " +
                "COALESCE(r.reward_status, 'PENDING') AS reward_status, " +
                "r.awarded_at " +
                "FROM bug_reports b " +
                "LEFT JOIN bug_rewards r " +
                "ON b.bug_id = r.bug_id " +
                "WHERE b.bug_id = ? " +
                "AND b.user_id = ?";

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

            statement.setInt(
                    2,
                    userId
            );

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return new String[] {

                    // 0 - Bug ID
                    String.valueOf(
                            resultSet.getInt(
                                    "bug_id"
                            )
                    ),

                    // 1 - Title
                    resultSet.getString(
                            "title"
                    ),

                    // 2 - Vulnerability Type
                    resultSet.getString(
                            "vulnerability_type"
                    ),

                    // 3 - Description
                    resultSet.getString(
                            "description"
                    ),

                    // 4 - Severity
                    resultSet.getString(
                            "severity"
                    ),

                    // 5 - Affected URL
                    resultSet.getString(
                            "affected_url"
                    ),

                    // 6 - Steps
                    resultSet.getString(
                            "steps_to_reproduce"
                    ),

                    // 7 - Evidence
                    resultSet.getString(
                            "evidence"
                    ),

                    // 8 - Bug Status
                    resultSet.getString(
                            "status"
                    ),

                    // 9 - Submitted At
                    resultSet.getString(
                            "submitted_at"
                    ),

                    // 10 - Reward Amount
                    String.valueOf(
                            resultSet.getDouble(
                                    "reward_amount"
                            )
                    ),

                    // 11 - Reward Status
                    resultSet.getString(
                            "reward_status"
                    ),

                    // 12 - Awarded At
                    resultSet.getString(
                            "awarded_at"
                    )
                };
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
    public List<String[]> getRecentBugReportsByUserId(int userId) {

    List<String[]> reports = new ArrayList<>();

    String sql =
            "SELECT " +
            "b.bug_id, " +
            "b.title, " +
            "b.vulnerability_type, " +
            "b.severity, " +
            "b.status, " +
            "COALESCE(r.amount, 0) AS reward_amount, " +
            "COALESCE(r.reward_status, 'PENDING') AS reward_status, " +
            "b.submitted_at " +
            "FROM bug_reports b " +
            "LEFT JOIN bug_rewards r ON b.bug_id = r.bug_id " +
            "WHERE b.user_id = ? " +
            "ORDER BY b.submitted_at DESC " +
            "LIMIT 5";

    try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setInt(1, userId);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {

            String[] report = {
                String.valueOf(resultSet.getInt("bug_id")),
                resultSet.getString("title"),
                resultSet.getString("vulnerability_type"),
                resultSet.getString("severity"),
                resultSet.getString("status"),
                String.valueOf(
                    resultSet.getDouble("reward_amount")
                ),
                resultSet.getString("reward_status"),
                resultSet.getString("submitted_at")
            };

            reports.add(report);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return reports;
}
}