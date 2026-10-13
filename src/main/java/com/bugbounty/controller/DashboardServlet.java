package com.bugbounty.controller;

import com.bugbounty.dao.BugReportDAO;
import com.bugbounty.dao.RewardStatsDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private RewardStatsDAO rewardStatsDAO;
    private BugReportDAO bugReportDAO;

    @Override
    public void init() {
        rewardStatsDAO = new RewardStatsDAO();
        bugReportDAO = new BugReportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");

        if (role == null || !"HACKER".equalsIgnoreCase(role)) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        // Reward summary
        double totalReward =
                rewardStatsDAO.getTotalReward(userId);

        double pendingReward =
                rewardStatsDAO.getPendingReward(userId);

        double paidReward =
                rewardStatsDAO.getPaidReward(userId);

        // Recent bug reports
        List<String[]> recentReports =
                bugReportDAO.getRecentBugReportsByUserId(userId);

        request.setAttribute("totalReward", totalReward);
        request.setAttribute("pendingReward", pendingReward);
        request.setAttribute("paidReward", paidReward);

        request.setAttribute("recentReports", recentReports);

        request.getRequestDispatcher("dashboard.jsp")
               .forward(request, response);
    }
}