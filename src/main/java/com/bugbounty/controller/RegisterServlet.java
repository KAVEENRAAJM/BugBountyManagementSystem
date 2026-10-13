package com.bugbounty.controller;

import com.bugbounty.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {

        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String name =
                request.getParameter("name");

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");

        boolean registered =
                userDAO.registerUser(
                        name,
                        email,
                        password
                );

        if (registered) {

            response.sendRedirect(
                    "login.jsp?success=registered"
            );

        } else {

            response.sendRedirect(
                    "register.jsp?error=failed"
            );
        }
    }
}