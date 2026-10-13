package com.bugbounty.controller;

import com.bugbounty.dao.BugReportDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/myReports")
public class MyReportsServlet extends HttpServlet {

    private BugReportDAO bugReportDAO;

    @Override
    public void init() {

        bugReportDAO =
                new BugReportDAO();
    }


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {


        // =====================================
        // CHECK LOGIN SESSION
        // =====================================

        HttpSession session =
                request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect("login.jsp");
            return;
        }


        // =====================================
        // GET LOGGED-IN USER ID
        // =====================================

        int userId =
                (Integer) session.getAttribute(
                        "userId"
                );


        // =====================================
        // GET USER'S BUG REPORTS
        // =====================================

        List<String[]> reports =
                bugReportDAO.getBugReportsByUserId(
                        userId
                );


        // =====================================
        // SEND REPORTS TO JSP
        // =====================================

        request.setAttribute(
                "myReports",
                reports
        );


        // =====================================
        // OPEN MY REPORTS PAGE
        // =====================================

        request.getRequestDispatcher(
                "my-reports.jsp"
        ).forward(
                request,
                response
        );
    }
}