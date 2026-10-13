package com.bugbounty.controller;

import java.io.IOException;
import java.util.List;

import com.bugbounty.dao.AdminDAO;
import com.bugbounty.dao.EvidenceDAO;
import com.bugbounty.dao.EvidenceDAO.Evidence;
import com.bugbounty.dao.RewardDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/bugDetails")
public class BugDetailsServlet extends HttpServlet {

    private AdminDAO adminDAO;
    private RewardDAO rewardDAO;
    private EvidenceDAO evidenceDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
        rewardDAO = new RewardDAO();
        evidenceDAO = new EvidenceDAO();
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

        // Only admins may access this page.
        String role = String.valueOf(session.getAttribute("role"));

        if (!"ADMIN".equalsIgnoreCase(role)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required."
            );
            return;
        }

        // Validate bug ID.
        String bugIdParameter = request.getParameter("bugId");

        if (bugIdParameter == null ||
                bugIdParameter.trim().isEmpty()) {

            response.sendRedirect("admin");
            return;
        }

        try {
            int bugId = Integer.parseInt(bugIdParameter);

            if (bugId <= 0) {
                response.sendRedirect("admin");
                return;
            }

            // Load bug report.
            String[] bug = adminDAO.getBugDetails(bugId);

            if (bug == null) {
                response.sendRedirect("admin?error=notfound");
                return;
            }

            // Load reward information.
            String[] reward = rewardDAO.getReward(bugId);

            // Load uploaded evidence metadata.
            List<Evidence> evidenceList =
                    evidenceDAO.getEvidenceByBugId(bugId);

            // Pass all data to the admin JSP.
            request.setAttribute("bug", bug);
            request.setAttribute("rewardDetails", reward);
            request.setAttribute("evidenceList", evidenceList);

            request.getRequestDispatcher("bug-details.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("admin");

        } catch (Exception e) {
            getServletContext().log(
                    "Error loading admin bug details.",
                    e
            );

            response.sendRedirect("admin?error=server");
        }
    }
}
