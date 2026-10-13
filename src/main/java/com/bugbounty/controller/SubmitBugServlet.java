package com.bugbounty.controller;

import java.io.IOException;

import com.bugbounty.dao.ActivityLogDAO;
import com.bugbounty.dao.BugReportDAO;
import com.bugbounty.util.CsrfTokenUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/submitBug")
public class SubmitBugServlet extends HttpServlet {

    private BugReportDAO bugReportDAO;
    private ActivityLogDAO activityLogDAO;

    @Override
    public void init() throws ServletException {
        bugReportDAO = new BugReportDAO();
        activityLogDAO = new ActivityLogDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Require an existing authenticated HACKER session.
        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null
                || session.getAttribute("email") == null
                || !"HACKER".equalsIgnoreCase(
                        String.valueOf(session.getAttribute("role")))) {

            response.sendRedirect("login.jsp");
            return;
        }

        // Validate the CSRF token before processing any form data.
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
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid session. Please log in again."
            );
            return;
        }

        String title = request.getParameter("title");
        String vulnerabilityType =
                request.getParameter("vulnerabilityType");
        String description = request.getParameter("description");
        String severity = request.getParameter("severity");
        String affectedUrl = request.getParameter("affectedUrl");
        String steps = request.getParameter("steps");
        String evidence = request.getParameter("evidence");

        boolean submitted = bugReportDAO.submitBug(
                userId,
                title,
                vulnerabilityType,
                description,
                severity,
                affectedUrl,
                steps,
                evidence
        );

        if (submitted) {

            // Keep activity logging without breaking a successful submission
            // if logging encounters a database error.
            try {
                activityLogDAO.logActivity(
                        userId,
                        "BUG_SUBMITTED",
                        "Submitted bug report: "
                                + (title == null ? "(untitled)" : title),
                        null
                );
            } catch (Exception e) {
                getServletContext().log(
                        "Bug report submitted, but activity logging failed.",
                        e
                );
            }

            response.sendRedirect(
                    "dashboard.jsp?success=bugsubmitted"
            );

        } else {
            response.sendRedirect(
                    "submit-bug.jsp?error=failed"
            );
        }
    }
}
