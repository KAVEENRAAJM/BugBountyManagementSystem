
package com.bugbounty.controller;

import java.io.IOException;
import java.util.Locale;

import com.bugbounty.dao.ActivityLogDAO;
import com.bugbounty.dao.BugReportAdminDAO;
import com.bugbounty.util.CsrfTokenUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/updateBugStatus")
public class UpdateBugStatusServlet extends HttpServlet {

    private BugReportAdminDAO bugReportAdminDAO;
    private ActivityLogDAO activityLogDAO;

    @Override
    public void init() throws ServletException {
        bugReportAdminDAO = new BugReportAdminDAO();
        activityLogDAO = new ActivityLogDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Check login session.
        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        // Only admins can change bug status.
        String role = String.valueOf(session.getAttribute("role"));

        if (!"ADMIN".equalsIgnoreCase(role)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required."
            );
            return;
        }

        // Validate CSRF token before processing the form.
        if (!CsrfTokenUtil.isValid(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or missing CSRF token. Reload the form and try again."
            );
            return;
        }

        int adminId;

        try {
            adminId = Integer.parseInt(
                    String.valueOf(session.getAttribute("userId"))
            );
        } catch (NumberFormatException e) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Validate bug ID.
        String bugIdParameter = request.getParameter("bugId");

        if (bugIdParameter == null ||
                bugIdParameter.trim().isEmpty()) {

            response.sendRedirect("admin?error=update");
            return;
        }

        // Validate requested status.
        String status = request.getParameter("status");

        if (status == null || status.trim().isEmpty()) {
            response.sendRedirect("admin?error=update");
            return;
        }

        status = status.trim().toUpperCase(Locale.ROOT);

        if (!status.equals("SUBMITTED") &&
                !status.equals("UNDER REVIEW") &&
                !status.equals("VERIFIED") &&
                !status.equals("REJECTED") &&
                !status.equals("RESOLVED")) {

            response.sendRedirect("admin?error=update");
            return;
        }

        try {
            int bugId = Integer.parseInt(bugIdParameter);

            if (bugId <= 0) {
                response.sendRedirect("admin?error=update");
                return;
            }

            // Update the report status.
            boolean updated = bugReportAdminDAO.updateStatus(
                    bugId,
                    status
            );

            if (updated) {

                // Record the successful status update.
                try {
                    boolean logged = activityLogDAO.logActivity(
                            adminId,
                            "BUG_STATUS_UPDATED",
                            "Admin " + adminId +
                                    " set bug report status to " + status + ".",
                            bugId
                    );

                    if (!logged) {
                        getServletContext().log(
                                "Bug status updated, but activity logging "
                                        + "failed for bug ID " + bugId
                        );
                    }

                } catch (Exception e) {
                    getServletContext().log(
                            "Bug status updated, but activity logging failed.",
                            e
                    );
                }

                response.sendRedirect("admin");

            } else {
                response.sendRedirect("admin?error=update");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("admin?error=update");

        } catch (Exception e) {
            getServletContext().log(
                    "Unable to update bug report status.",
                    e
            );

            response.sendRedirect("admin?error=update");
        }
    }
}
