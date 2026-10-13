package com.bugbounty.controller;

import com.bugbounty.dao.AdminDAO;
import com.bugbounty.dao.AdminRewardStatsDAO;
import com.bugbounty.dao.AdminStatsDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private AdminDAO adminDAO;
    private AdminStatsDAO adminStatsDAO;
    private AdminRewardStatsDAO adminRewardStatsDAO;

    @Override
    public void init() {
        adminDAO = new AdminDAO();
        adminStatsDAO = new AdminStatsDAO();
        adminRewardStatsDAO = new AdminRewardStatsDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // =========================================
        // SESSION CHECK
        // =========================================

        HttpSession session =
                request.getSession(false);

        if (session == null ||
            session.getAttribute("email") == null) {

            response.sendRedirect("login.jsp");
            return;
        }


        // =========================================
        // ADMIN ROLE CHECK
        // =========================================

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
            !"ADMIN".equalsIgnoreCase(role)) {

            response.sendRedirect("dashboard.jsp");
            return;
        }


        // =========================================
        // SEARCH & FILTER VALUES
        // =========================================

        String search =
                request.getParameter("search");

        String status =
                request.getParameter("status");

        String severity =
                request.getParameter("severity");


        // =========================================
        // GET BUG REPORTS
        // =========================================

        List<String[]> reports;

        if ((search == null || search.trim().isEmpty()) &&
            (status == null || status.trim().isEmpty()) &&
            (severity == null || severity.trim().isEmpty())) {

            reports =
                    adminDAO.getAllBugReports();

        } else {

            reports =
                    adminDAO.searchBugReports(
                            search,
                            status,
                            severity
                    );
        }


        // =========================================
        // BUG REPORT STATISTICS
        // =========================================

        int totalReports =
                adminStatsDAO.getTotalReports();

        int submittedReports =
                adminStatsDAO.getSubmittedReports();

        int underReviewReports =
                adminStatsDAO.getUnderReviewReports();

        int verifiedReports =
                adminStatsDAO.getVerifiedReports();

        int resolvedReports =
                adminStatsDAO.getResolvedReports();

        int rejectedReports =
                adminStatsDAO.getRejectedReports();


        // =========================================
        // REWARD STATISTICS
        // =========================================

        double totalBounty =
                adminRewardStatsDAO.getTotalBounty();

        double pendingBounty =
                adminRewardStatsDAO.getPendingBounty();

        double paidBounty =
                adminRewardStatsDAO.getPaidBounty();


        // =========================================
        // SEND DATA TO JSP
        // =========================================

        request.setAttribute(
                "reports",
                reports
        );

        request.setAttribute(
                "search",
                search
        );

        request.setAttribute(
                "selectedStatus",
                status
        );

        request.setAttribute(
                "selectedSeverity",
                severity
        );


        // Bug statistics

        request.setAttribute(
                "totalReports",
                totalReports
        );

        request.setAttribute(
                "submittedReports",
                submittedReports
        );

        request.setAttribute(
                "underReviewReports",
                underReviewReports
        );

        request.setAttribute(
                "verifiedReports",
                verifiedReports
        );

        request.setAttribute(
                "resolvedReports",
                resolvedReports
        );

        request.setAttribute(
                "rejectedReports",
                rejectedReports
        );


        // Reward statistics

        request.setAttribute(
                "totalBounty",
                totalBounty
        );

        request.setAttribute(
                "pendingBounty",
                pendingBounty
        );

        request.setAttribute(
                "paidBounty",
                paidBounty
        );


        // =========================================
        // FORWARD TO ADMIN DASHBOARD
        // =========================================

        request.getRequestDispatcher(
                "admin-dashboard.jsp"
        ).forward(request, response);
    }
}