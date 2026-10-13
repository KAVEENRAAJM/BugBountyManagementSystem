package com.bugbounty.dao;

import com.bugbounty.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // =========================================================
    // GET ALL BUG REPORTS
    // =========================================================

    public List<String[]> getAllBugReports() {

        List<String[]> reports = new ArrayList<>();

        String sql =
                "SELECT " +
                "b.bug_id, " +
                "u.name AS hacker_name, " +
                "b.title, " +
                "b.vulnerability_type, " +
                "b.severity, " +
                "b.status, " +
                "b.submitted_at, " +
                "COALESCE(r.amount, 0) AS reward_amount, " +
                "COALESCE(r.reward_status, 'PENDING') AS reward_status " +
                "FROM bug_reports b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "LEFT JOIN bug_rewards r ON b.bug_id = r.bug_id " +
                "ORDER BY b.submitted_at DESC";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                String[] report = {

                        String.valueOf(
                                resultSet.getInt("bug_id")
                        ),

                        resultSet.getString("hacker_name"),

                        resultSet.getString("title"),

                        resultSet.getString(
                                "vulnerability_type"
                        ),

                        resultSet.getString("severity"),

                        resultSet.getString("status"),

                        resultSet.getString("submitted_at"),

                        String.valueOf(
                                resultSet.getDouble("reward_amount")
                        ),

                        resultSet.getString("reward_status")
                };

                reports.add(report);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reports;
    }


    // =========================================================
    // GET BUG DETAILS
    // =========================================================

    public String[] getBugDetails(int bugId) {

        String sql =
                "SELECT " +
                "b.bug_id, " +
                "u.name AS hacker_name, " +
                "u.email, " +
                "b.title, " +
                "b.vulnerability_type, " +
                "b.description, " +
                "b.severity, " +
                "b.affected_url, " +
                "b.steps_to_reproduce, " +
                "b.evidence, " +
                "b.status, " +
                "b.submitted_at " +
                "FROM bug_reports b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "WHERE b.bug_id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, bugId);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return new String[] {

                        String.valueOf(
                                resultSet.getInt("bug_id")
                        ),

                        resultSet.getString("hacker_name"),

                        resultSet.getString("email"),

                        resultSet.getString("title"),

                        resultSet.getString(
                                "vulnerability_type"
                        ),

                        resultSet.getString("description"),

                        resultSet.getString("severity"),

                        resultSet.getString("affected_url"),

                        resultSet.getString(
                                "steps_to_reproduce"
                        ),

                        resultSet.getString("evidence"),

                        resultSet.getString("status"),

                        resultSet.getString("submitted_at")
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // =========================================================
    // SEARCH AND FILTER BUG REPORTS
    // =========================================================

    public List<String[]> searchBugReports(
            String search,
            String status,
            String severity) {

        List<String[]> reports = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(
                "SELECT " +
                "b.bug_id, " +
                "u.name AS hacker_name, " +
                "b.title, " +
                "b.vulnerability_type, " +
                "b.severity, " +
                "b.status, " +
                "b.submitted_at, " +
                "COALESCE(r.amount, 0) AS reward_amount, " +
                "COALESCE(r.reward_status, 'PENDING') AS reward_status " +
                "FROM bug_reports b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "LEFT JOIN bug_rewards r ON b.bug_id = r.bug_id " +
                "WHERE 1=1 "
        );


        // -----------------------------------------------------
        // SEARCH FILTER
        // -----------------------------------------------------

        if (search != null && !search.trim().isEmpty()) {

            sql.append(
                    "AND (" +
                    "b.title LIKE ? " +
                    "OR b.vulnerability_type LIKE ? " +
                    "OR u.name LIKE ? " +
                    ") "
            );
        }


        // -----------------------------------------------------
        // STATUS FILTER
        // -----------------------------------------------------

        if (status != null &&
            !status.trim().isEmpty()) {

            sql.append(
                    "AND b.status = ? "
            );
        }


        // -----------------------------------------------------
        // SEVERITY FILTER
        // -----------------------------------------------------

        if (severity != null &&
            !severity.trim().isEmpty()) {

            sql.append(
                    "AND b.severity = ? "
            );
        }


        sql.append(
                "ORDER BY b.submitted_at DESC"
        );


        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql.toString())
        ) {

            int parameterIndex = 1;


            // -------------------------------------------------
            // SEARCH PARAMETERS
            // -------------------------------------------------

            if (search != null &&
                !search.trim().isEmpty()) {

                String searchPattern =
                        "%" + search.trim() + "%";

                statement.setString(
                        parameterIndex++,
                        searchPattern
                );

                statement.setString(
                        parameterIndex++,
                        searchPattern
                );

                statement.setString(
                        parameterIndex++,
                        searchPattern
                );
            }


            // -------------------------------------------------
            // STATUS PARAMETER
            // -------------------------------------------------

            if (status != null &&
                !status.trim().isEmpty()) {

                statement.setString(
                        parameterIndex++,
                        status
                );
            }


            // -------------------------------------------------
            // SEVERITY PARAMETER
            // -------------------------------------------------

            if (severity != null &&
                !severity.trim().isEmpty()) {

                statement.setString(
                        parameterIndex++,
                        severity
                );
            }


            ResultSet resultSet =
                    statement.executeQuery();


            while (resultSet.next()) {

                String[] report = {

                        String.valueOf(
                                resultSet.getInt("bug_id")
                        ),

                        resultSet.getString("hacker_name"),

                        resultSet.getString("title"),

                        resultSet.getString(
                                "vulnerability_type"
                        ),

                        resultSet.getString("severity"),

                        resultSet.getString("status"),

                        resultSet.getString("submitted_at"),

                        String.valueOf(
                                resultSet.getDouble(
                                        "reward_amount"
                                )
                        ),

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
}