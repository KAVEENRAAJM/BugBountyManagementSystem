package com.bugbounty.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bugbounty.util.DBConnection;

public class EvidenceDAO {

    public static class Evidence {

        private final int evidenceId;
        private final int bugId;
        private final String originalFilename;
        private final String storedFilename;
        private final String contentType;
        private final long fileSize;
        private final String uploadedAt;

        public Evidence(
                int evidenceId,
                int bugId,
                String originalFilename,
                String storedFilename,
                String contentType,
                long fileSize,
                String uploadedAt) {

            this.evidenceId = evidenceId;
            this.bugId = bugId;
            this.originalFilename = originalFilename;
            this.storedFilename = storedFilename;
            this.contentType = contentType;
            this.fileSize = fileSize;
            this.uploadedAt = uploadedAt;
        }

        public int getEvidenceId() {
            return evidenceId;
        }

        public int getBugId() {
            return bugId;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public String getStoredFilename() {
            return storedFilename;
        }

        public String getContentType() {
            return contentType;
        }

        public long getFileSize() {
            return fileSize;
        }

        public String getUploadedAt() {
            return uploadedAt;
        }
    }

    // Save evidence metadata after a successful file upload.
    public boolean saveEvidence(
            int bugId,
            String originalFilename,
            String storedFilename,
            String contentType,
            long fileSize) {

        String sql =
                "INSERT INTO bug_evidence " +
                "(bug_id, original_filename, stored_filename, " +
                "content_type, file_size) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, bugId);
                statement.setString(2, originalFilename);
                statement.setString(3, storedFilename);
                statement.setString(4, contentType);
                statement.setLong(5, fileSize);

                return statement.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve every evidence file belonging to a bug report.
    public List<Evidence> getEvidenceByBugId(int bugId) {

        List<Evidence> evidenceList = new ArrayList<>();

        String sql =
                "SELECT evidence_id, bug_id, original_filename, " +
                "stored_filename, content_type, file_size, uploaded_at " +
                "FROM bug_evidence " +
                "WHERE bug_id = ? " +
                "ORDER BY uploaded_at DESC";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return evidenceList;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, bugId);

                try (ResultSet rs = statement.executeQuery()) {

                    while (rs.next()) {

                        evidenceList.add(
                                new Evidence(
                                        rs.getInt("evidence_id"),
                                        rs.getInt("bug_id"),
                                        rs.getString("original_filename"),
                                        rs.getString("stored_filename"),
                                        rs.getString("content_type"),
                                        rs.getLong("file_size"),
                                        rs.getString("uploaded_at")
                                )
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return evidenceList;
    }

    // Retrieve one evidence record by its ID.
    public Evidence getEvidenceById(int evidenceId) {

        String sql =
                "SELECT evidence_id, bug_id, original_filename, " +
                "stored_filename, content_type, file_size, uploaded_at " +
                "FROM bug_evidence " +
                "WHERE evidence_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return null;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, evidenceId);

                try (ResultSet rs = statement.executeQuery()) {

                    if (rs.next()) {

                        return new Evidence(
                                rs.getInt("evidence_id"),
                                rs.getInt("bug_id"),
                                rs.getString("original_filename"),
                                rs.getString("stored_filename"),
                                rs.getString("content_type"),
                                rs.getLong("file_size"),
                                rs.getString("uploaded_at")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
