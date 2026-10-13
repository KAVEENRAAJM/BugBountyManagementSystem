package com.bugbounty.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bugbounty.dao.EvidenceDAO;
import com.bugbounty.dao.EvidenceDAO.Evidence;
import com.bugbounty.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/downloadEvidence")
public class DownloadEvidenceServlet extends HttpServlet {

    private final EvidenceDAO evidenceDAO = new EvidenceDAO();

    private static final Path UPLOAD_DIRECTORY =
            Paths.get(
                    System.getProperty("user.home"),
                    "bugbounty-evidence"
            ).toAbsolutePath().normalize();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null ||
                session.getAttribute("role") == null) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in to access evidence."
            );
            return;
        }

        int evidenceId;

        try {
            evidenceId = Integer.parseInt(
                    request.getParameter("evidenceId")
            );

            if (evidenceId <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid evidence ID."
                );
                return;
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid evidence ID."
            );
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(
                    String.valueOf(session.getAttribute("userId"))
            );
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        String role = String.valueOf(
                session.getAttribute("role")
        );

        Evidence evidence = evidenceDAO.getEvidenceById(evidenceId);

        if (evidence == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Evidence not found."
            );
            return;
        }

        if (!canAccessEvidence(evidence.getBugId(), userId, role)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You are not authorized to access this evidence."
            );
            return;
        }

        String storedFilename = evidence.getStoredFilename();

        // Only permit server-generated UUID filenames and approved extensions.
        if (storedFilename == null ||
                !storedFilename.matches(
                        "^[0-9a-fA-F-]{36}\\.(png|jpg|jpeg|pdf)$"
                )) {

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Invalid evidence storage reference."
            );
            return;
        }

        Path filePath = UPLOAD_DIRECTORY
                .resolve(storedFilename)
                .normalize();

        if (!filePath.startsWith(UPLOAD_DIRECTORY) ||
                !Files.isRegularFile(filePath) ||
                !Files.isReadable(filePath)) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Evidence file is unavailable."
            );
            return;
        }

        String contentType = evidence.getContentType();

        if (!"image/png".equals(contentType) &&
                !"image/jpeg".equals(contentType) &&
                !"application/pdf".equals(contentType)) {

            response.sendError(
                    HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported evidence format."
            );
            return;
        }

        String originalFilename = evidence.getOriginalFilename();

        // Prevent header injection through the uploaded filename.
        originalFilename = originalFilename == null
                ? "evidence"
                : originalFilename.replaceAll("[\\r\\n\"\\\\]", "_");

        response.setContentType(contentType);
        response.setHeader(
                "X-Content-Type-Options",
                "nosniff"
        );
        response.setHeader(
                "Cache-Control",
                "private, no-store"
        );
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + originalFilename + "\""
        );
        response.setContentLengthLong(Files.size(filePath));

        try (
                InputStream input = Files.newInputStream(filePath);
                OutputStream output = response.getOutputStream()
        ) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }

    private boolean canAccessEvidence(
            int bugId,
            int userId,
            String role) {

        String sql;

        if ("ADMIN".equalsIgnoreCase(role)) {
            sql = "SELECT 1 FROM bug_reports WHERE bug_id = ?";
        } else if ("HACKER".equalsIgnoreCase(role)) {
            sql = "SELECT 1 FROM bug_reports WHERE bug_id = ? AND user_id = ?";
        } else {
            return false;
        }

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, bugId);

                if ("HACKER".equalsIgnoreCase(role)) {
                    statement.setInt(2, userId);
                }

                try (ResultSet rs = statement.executeQuery()) {
                    return rs.next();
                }
            }

        } catch (Exception e) {
            getServletContext().log(
                    "Unable to verify evidence access.",
                    e
            );
            return false;
        }
    }
}