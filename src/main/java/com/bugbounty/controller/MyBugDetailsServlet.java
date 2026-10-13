package com.bugbounty.controller;

import java.io.IOException;
import java.util.List;

import com.bugbounty.dao.BugReportDAO;
import com.bugbounty.dao.EvidenceDAO;
import com.bugbounty.dao.EvidenceDAO.Evidence;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/myBugDetails")
public class MyBugDetailsServlet extends HttpServlet {

    private BugReportDAO bugReportDAO;
    private EvidenceDAO evidenceDAO;

    @Override
    public void init() throws ServletException {
        bugReportDAO = new BugReportDAO();
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
                !"HACKER".equalsIgnoreCase(
                        String.valueOf(session.getAttribute("role")))) {

            response.sendRedirect("login.jsp");
            return;
        }

        // Get the logged-in hacker's ID.
        int userId;

        try {
            userId = Integer.parseInt(
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

            response.sendRedirect("myReports");
            return;
        }

        try {
            int bugId = Integer.parseInt(bugIdParameter);

            if (bugId <= 0) {
                response.sendRedirect("myReports");
                return;
            }

            // Fetch only a report owned by this hacker.
            String[] bug =
                    bugReportDAO.getBugDetailsByUserId(
                            bugId,
                            userId
                    );

            if (bug == null) {
                response.sendRedirect("myReports?error=notfound");
                return;
            }

            // Load evidence metadata only after ownership is verified.
            List<Evidence> evidenceList =
                    evidenceDAO.getEvidenceByBugId(bugId);

            request.setAttribute("myBug", bug);
            request.setAttribute("evidenceList", evidenceList);

            request.getRequestDispatcher(
                    "my-bug-details.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("myReports");

        } catch (Exception e) {
            getServletContext().log(
                    "Error loading bug details and evidence.",
                    e
            );

            response.sendRedirect("myReports?error=server");
        }
    }
}