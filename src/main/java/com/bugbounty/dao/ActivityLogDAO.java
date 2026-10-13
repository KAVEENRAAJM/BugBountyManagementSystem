package com.bugbounty.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bugbounty.util.DBConnection;

public class ActivityLogDAO {

    /**
     * Represents one activity log record.
     */
    public static class ActivityLog {

        private final int logId;
        private final Integer userId;
        private final String action;
        private final String description;
        private final Integer bugId;
        private final String createdAt;

        public ActivityLog(
                int logId,
                Integer userId,
                String action,
                String description,
                Integer bugId,
                String createdAt) {

            this.logId = logId;
            this.userId = userId;
            this.action = action;
            this.description = description;
            this.bugId = bugId;
            this.createdAt = createdAt;
        }

        public int getLogId() {
            return logId;
        }

        public Integer getUserId() {
            return userId;
        }

        public String getAction() {
            return action;
        }

        public String getDescription() {
            return description;
        }

        public Integer getBugId() {
            return bugId;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    /**
     * Records an activity performed by a user.
     */
    public boolean logActivity(
            Integer userId,
            String action,
            String description,
            Integer bugId) {

        if (action == null || action.trim().isEmpty()) {
            return false;
        }

        String sql =
                "INSERT INTO activity_logs " +
                "(user_id, action, description, bug_id) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                if (userId == null) {
                    statement.setNull(1, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(1, userId);
                }

                statement.setString(2, action.trim());
                statement.setString(3, description);

                if (bugId == null) {
                    statement.setNull(4, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(4, bugId);
                }

                return statement.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves activity records using optional action and search filters.
     *
     * @param actionFilter exact action type, or null for all actions
     * @param searchText   text to search in description/action, or null
     * @return matching activity records, newest first
     */
    public List<ActivityLog> getActivityLogs(
            String actionFilter,
            String searchText) {

        List<ActivityLog> logs = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT log_id, user_id, action, description, " +
                "bug_id, created_at " +
                "FROM activity_logs WHERE 1 = 1 "
        );

        boolean filterAction =
                actionFilter != null &&
                !actionFilter.trim().isEmpty() &&
                !"ALL".equalsIgnoreCase(actionFilter.trim());

        boolean filterSearch =
                searchText != null &&
                !searchText.trim().isEmpty();

        if (filterAction) {
            sql.append("AND action = ? ");
        }

        if (filterSearch) {
            sql.append(
                    "AND (action LIKE ? OR description LIKE ?) "
            );
        }

        sql.append("ORDER BY created_at DESC, log_id DESC LIMIT 500");

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return logs;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql.toString())) {

                int parameterIndex = 1;

                if (filterAction) {
                    statement.setString(
                            parameterIndex++,
                            actionFilter.trim()
                    );
                }

                if (filterSearch) {
                    String searchPattern =
                            "%" + searchText.trim() + "%";

                    statement.setString(
                            parameterIndex++,
                            searchPattern
                    );

                    statement.setString(
                            parameterIndex,
                            searchPattern
                    );
                }

                try (ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {

                        int userIdValue =
                                resultSet.getInt("user_id");

                        Integer userId =
                                resultSet.wasNull()
                                        ? null
                                        : userIdValue;

                        int bugIdValue =
                                resultSet.getInt("bug_id");

                        Integer bugId =
                                resultSet.wasNull()
                                        ? null
                                        : bugIdValue;

                        java.sql.Timestamp timestamp =
                                resultSet.getTimestamp("created_at");

                        String createdAt =
                                timestamp == null
                                        ? ""
                                        : timestamp.toString();

                        ActivityLog log = new ActivityLog(
                                resultSet.getInt("log_id"),
                                userId,
                                resultSet.getString("action"),
                                resultSet.getString("description"),
                                bugId,
                                createdAt
                        );

                        logs.add(log);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
}
