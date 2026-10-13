package com.bugbounty.controller;

import java.io.IOException;
import java.util.List;

import com.bugbounty.dao.ActivityLogDAO;
import com.bugbounty.dao.ActivityLogDAO.ActivityLog;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/activityLogs")
public class ActivityLogsServlet extends HttpServlet {

    private ActivityLogDAO activityLogDAO;

    @Override
    public void init() throws ServletException {
        activityLogDAO = new ActivityLogDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Check login session.
        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null ||
                session.getAttribute("email") == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        // Only admins may view activity logs.
        String role = String.valueOf(session.getAttribute("role"));

        if (!"ADMIN".equalsIgnoreCase(role)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required."
            );
            return;
        }

        String actionFilter = request.getParameter("action");
        String searchText = request.getParameter("search");

        // Load records using the requested filters.
        List<ActivityLog> logs =
                activityLogDAO.getActivityLogs(
                        actionFilter,
                        searchText
                );

        request.setAttribute("activityLogs", logs);
        request.setAttribute("selectedAction",
                actionFilter == null ? "ALL" : actionFilter);
        request.setAttribute("searchText",
                searchText == null ? "" : searchText);

        request.getRequestDispatcher(
                "/activity-logs.jsp"
        ).forward(request, response);
    }
}