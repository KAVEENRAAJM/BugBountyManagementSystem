
package com.bugbounty.controller;

import java.io.IOException;

import com.bugbounty.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long SESSION_TIMEOUT_SECONDS = 15 * 60;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Reject missing or blank credentials.
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {

            response.sendRedirect("login.jsp?error=invalid");
            return;
        }

        email = email.trim();

        // Verify credentials using the existing UserDAO.
        // The DAO remains responsible for password verification.
        String role = userDAO.loginUser(email, password);

        if (role == null) {
            response.sendRedirect("login.jsp?error=invalid");
            return;
        }

        // Accept only roles supported by this application.
        if (!"ADMIN".equalsIgnoreCase(role)
                && !"HACKER".equalsIgnoreCase(role)) {

            response.sendRedirect("login.jsp?error=role");
            return;
        }

        // Retrieve the authenticated user's ID.
        int userId = userDAO.getUserId(email);

        if (userId <= 0) {
            response.sendRedirect("login.jsp?error=invalid");
            return;
        }

        /*
         * SESSION FIXATION PROTECTION:
         * Invalidate any existing session and create a new one
         * only after successful authentication.
         */
        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = request.getSession(true);

        // Set an idle timeout of 15 minutes.
        session.setMaxInactiveInterval(
                (int) SESSION_TIMEOUT_SECONDS
        );

        // Store only the authenticated user's required information.
        session.setAttribute("userId", userId);
        session.setAttribute("email", email);
        session.setAttribute("role", role.toUpperCase());

        // Redirect according to the authenticated role.
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect("admin");
            return;
        }

        if ("HACKER".equalsIgnoreCase(role)) {
            response.sendRedirect("dashboard");
        }
    }
}
