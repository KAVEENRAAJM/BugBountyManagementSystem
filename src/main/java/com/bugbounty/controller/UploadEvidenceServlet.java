package com.bugbounty.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.UUID;

import com.bugbounty.dao.ActivityLogDAO;
import com.bugbounty.dao.EvidenceDAO;
import com.bugbounty.util.CsrfTokenUtil;
import com.bugbounty.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/uploadEvidence")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class UploadEvidenceServlet extends HttpServlet {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final Path UPLOAD_DIRECTORY =
            Paths.get(
                    System.getProperty("user.home"),
                    "bugbounty-evidence"
            ).toAbsolutePath().normalize();

    private EvidenceDAO evidenceDAO;
    private ActivityLogDAO activityLogDAO;

    @Override
    public void init() throws ServletException {
        evidenceDAO = new EvidenceDAO();
        activityLogDAO = new ActivityLogDAO();

        try {
            Files.createDirectories(UPLOAD_DIRECTORY);
        } catch (IOException e) {
            throw new ServletException(
                    "Unable to initialize evidence storage.",
                    e
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null ||
                !"HACKER".equalsIgnoreCase(
                        String.valueOf(session.getAttribute("role")))) {

            response.sendRedirect("login.jsp");
            return;
        }

        // Reject missing or invalid CSRF tokens before processing uploads.
        if (!CsrfTokenUtil.isValid(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or missing CSRF token. Reload the form and try again."
            );
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(
                    String.valueOf(session.getAttribute("userId"))
            );
        } catch (NumberFormatException e) {
            response.sendRedirect("login.jsp");
            return;
        }

        int bugId;

        try {
            bugId = Integer.parseInt(request.getParameter("bugId"));

            if (bugId <= 0) {
                response.sendRedirect("myReports?error=evidence");
                return;
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("myReports?error=evidence");
            return;
        }

        // Only the owner of the bug report can upload evidence.
        if (!ownsBugReport(bugId, userId)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You cannot upload evidence to this bug report."
            );
            return;
        }

        Part filePart;

        try {
            filePart = request.getPart("evidenceFile");
        } catch (IllegalStateException e) {
            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&error=file"
            );
            return;
        }

        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&error=empty"
            );
            return;
        }

        if (filePart.getSize() > MAX_FILE_SIZE) {
            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&error=filesize"
            );
            return;
        }

        String submittedName = filePart.getSubmittedFileName();

        if (submittedName == null || submittedName.trim().isEmpty()) {
            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&error=filename"
            );
            return;
        }

        // Keep only the filename, never the submitted path.
        String originalFilename = Paths.get(
                submittedName.replace("\\", "/")
        ).getFileName().toString();

        if (originalFilename.isBlank() ||
                originalFilename.length() > 255) {

            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&error=filename"
            );
            return;
        }

        String extension = getExtension(originalFilename);

        if (!extension.equals("png") &&
                !extension.equals("jpg") &&
                !extension.equals("jpeg") &&
                !extension.equals("pdf")) {

            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&error=filetype"
            );
            return;
        }

        // Verify the file signature.
        String detectedType;

        try (InputStream input = filePart.getInputStream()) {
            detectedType = detectFileType(input);
        }

        if (detectedType == null ||
                !extensionMatches(extension, detectedType)) {

            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&error=filetype"
            );
            return;
        }

        String storedFilename =
                UUID.randomUUID() + "." + extension;

        Path destination =
                UPLOAD_DIRECTORY.resolve(storedFilename).normalize();

        if (!destination.startsWith(UPLOAD_DIRECTORY)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean fileSaved = false;
        boolean metadataSaved = false;

        try {
            // Store the file outside the public web directory.
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(
                        input,
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                );

                fileSaved = true;
            }

            // Save the evidence metadata in MariaDB.
            metadataSaved = evidenceDAO.saveEvidence(
                    bugId,
                    originalFilename,
                    storedFilename,
                    detectedType,
                    filePart.getSize()
            );

            if (!metadataSaved) {
                response.sendRedirect(
                        "myBugDetails?bugId=" + bugId + "&error=save"
                );
                return;
            }

            // Record the upload only after both file and metadata succeed.
            // Logging failure must not turn a successful upload into a failure.
            try {
                boolean logged = activityLogDAO.logActivity(
                        userId,
                        "EVIDENCE_UPLOADED",
                        "Uploaded evidence file: " + originalFilename,
                        bugId
                );

                if (!logged) {
                    getServletContext().log(
                            "Evidence upload succeeded, but activity logging "
                                    + "failed for bug ID " + bugId
                    );
                }

            } catch (Exception e) {
                getServletContext().log(
                        "Evidence upload succeeded, but activity logging "
                                + "failed for bug ID " + bugId,
                        e
                );
            }

            response.sendRedirect(
                    "myBugDetails?bugId=" + bugId + "&evidence=success"
            );

        } catch (IOException e) {
            throw e;

        } finally {
            // Remove an orphaned file if its metadata was not saved.
            if (fileSaved &&
                    !metadataSaved &&
                    !metadataExists(bugId, storedFilename)) {

                Files.deleteIfExists(destination);
            }
        }
    }

    private boolean ownsBugReport(int bugId, int userId) {

        String sql =
                "SELECT 1 FROM bug_reports " +
                "WHERE bug_id = ? AND user_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, bugId);
                statement.setInt(2, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }

        } catch (Exception e) {
            getServletContext().log(
                    "Unable to verify bug report ownership.",
                    e
            );
            return false;
        }
    }

    private boolean metadataExists(
            int bugId,
            String storedFilename) {

        String sql =
                "SELECT 1 FROM bug_evidence " +
                "WHERE bug_id = ? AND stored_filename = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, bugId);
                statement.setString(2, storedFilename);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }

        } catch (Exception e) {
            getServletContext().log(
                    "Unable to verify evidence metadata.",
                    e
            );
            return false;
        }
    }

    private String getExtension(String filename) {

        int dot = filename.lastIndexOf('.');

        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }

        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private boolean extensionMatches(
            String extension,
            String detectedType) {

        if ("png".equals(extension)) {
            return "image/png".equals(detectedType);
        }

        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return "image/jpeg".equals(detectedType);
        }

        return "pdf".equals(extension)
                && "application/pdf".equals(detectedType);
    }

    private String detectFileType(InputStream input) throws IOException {

        byte[] header = input.readNBytes(8);

        // PNG signature.
        if (header.length >= 8 &&
                (header[0] & 0xFF) == 0x89 &&
                header[1] == 0x50 &&
                header[2] == 0x4E &&
                header[3] == 0x47 &&
                header[4] == 0x0D &&
                header[5] == 0x0A &&
                header[6] == 0x1A &&
                header[7] == 0x0A) {

            return "image/png";
        }

        // JPEG signature.
        if (header.length >= 3 &&
                (header[0] & 0xFF) == 0xFF &&
                (header[1] & 0xFF) == 0xD8 &&
                (header[2] & 0xFF) == 0xFF) {

            return "image/jpeg";
        }

        // PDF signature.
        if (header.length >= 5 &&
                header[0] == '%' &&
                header[1] == 'P' &&
                header[2] == 'D' &&
                header[3] == 'F' &&
                header[4] == '-') {

            return "application/pdf";
        }

        return null;
    }
}
