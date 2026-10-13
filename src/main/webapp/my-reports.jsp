<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> 
<%@ page import="java.util.List" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>

<%
    HttpSession sessionObj =
            request.getSession(false);

    if (sessionObj == null ||
        sessionObj.getAttribute("userId") == null) {

        response.sendRedirect("login.jsp");
        return;
    }

    String email =
            (String) sessionObj.getAttribute("email");

    List<String[]> reports =
            (List<String[]>) request.getAttribute(
                    "myReports"
            );
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        My Reports - BugBounty
    </title>

    <link rel="stylesheet"
          href="css/style.css">

</head>

<body>


<!-- =========================================
     NAVBAR
     ========================================= -->

<nav class="navbar">

    <div class="navbar-container">

        <div class="logo">
            BugBounty
        </div>

        <div class="nav-links">

            <a href="dashboard.jsp">
                Dashboard
            </a>

            <a href="myReports"
               class="active">
                My Reports
            </a>

            <a href="submit-bug.jsp">
                Submit Bug
            </a>

            <a href="logout">
                Logout
            </a>

        </div>

    </div>

</nav>


<!-- =========================================
     MAIN CONTENT
     ========================================= -->

<div class="container">


    <!-- =====================================
         HEADER
         ===================================== -->

    <div class="page-header">

        <div>

            <h1>
                My Bug Reports
            </h1>

            <p>
                Track your submitted vulnerabilities
                and bounty rewards.
            </p>

        </div>

        <div class="admin-info">

            Logged in as:

            <strong>
                <%= email %>
            </strong>

        </div>

    </div>


    <!-- =====================================
         REPORT TABLE
         ===================================== -->

    <div class="admin-table-container">

        <div class="table-header">

            <h2>
                Submitted Reports
            </h2>

            <span>

                <%= reports != null
                    ? reports.size()
                    : 0 %>

                report(s)

            </span>

        </div>


        <div class="table-wrapper">

            <table class="admin-table">

                <thead>

                    <tr>

                        <th>
                            ID
                        </th>

                        <th>
                            Bug Title
                        </th>

                        <th>
                            Vulnerability Type
                        </th>

                        <th>
                            Severity
                        </th>

                        <th>
                            Status
                        </th>

                        <th>
                            Bounty
                        </th>

                        <th>
                            Reward
                        </th>

                        <th>
                            Submitted At
                        </th>

                        <th>
                            Action
                        </th>

                    </tr>

                </thead>


                <tbody>


                <% if (reports != null &&
                       !reports.isEmpty()) { %>


                    <% for (String[] report : reports) { %>

                        <tr>


                            <!-- =================
                                 BUG ID
                                 ================= -->

                            <td>

                                #<%= report[0] %>

                            </td>


                            <!-- =================
                                 TITLE
                                 ================= -->

                            <td>

                                <strong>
                                    <%= report[1] %>
                                </strong>

                            </td>


                            <!-- =================
                                 VULNERABILITY
                                 ================= -->

                            <td>

                                <%= report[2] %>

                            </td>


                            <!-- =================
                                 SEVERITY
                                 ================= -->

                            <td>

                                <span
                                    class="status
                                    <%= report[3]
                                            .toLowerCase()
                                            .replace(" ", "-")
                                    %>">

                                    <%= report[3] %>

                                </span>

                            </td>


                            <!-- =================
                                 BUG STATUS
                                 ================= -->

                            <td>

                                <span
                                    class="status
                                    <%= report[4]
                                            .toLowerCase()
                                            .replace(" ", "-")
                                    %>">

                                    <%= report[4] %>

                                </span>

                            </td>


                            <!-- =================
                                 BOUNTY
                                 ================= -->

                            <td>

                                <strong>

                                    ₹<%= report[6] %>

                                </strong>

                            </td>


                            <!-- =================
                                 REWARD STATUS
                                 ================= -->

                            <td>

                                <span
                                    class="reward-status
                                    <%= report[7]
                                            .toLowerCase()
                                    %>">

                                    <%= report[7] %>

                                </span>

                            </td>


                            <!-- =================
                                 SUBMITTED
                                 ================= -->

                            <td>

                                <%= report[5] %>

                            </td>


                            <!-- =================
                                 ACTION
                                 ================= -->

                            <td>

                                <a
                                    href="myBugDetails?bugId=<%= report[0] %>"
                                    class="small-button view-button">

                                    View Details

                                </a>

                            </td>


                        </tr>

                    <% } %>


                <% } else { %>


                    <tr>

                        <td
                            colspan="9"
                            class="empty-message">

                            You haven't submitted
                            any bug reports yet.

                        </td>

                    </tr>


                <% } %>


                </tbody>

            </table>

        </div>

    </div>


    <!-- =====================================
         ACTIONS
         ===================================== -->

    <div class="details-actions">

        <a
            href="dashboard.jsp"
            class="btn secondary">

            ← Back to Dashboard

        </a>

        <a
            href="submit-bug.jsp"
            class="btn">

            + Submit New Bug

        </a>

    </div>


</div>


<!-- =========================================
     FOOTER
     ========================================= -->

<footer>

    <p>
        © 2026 BugBounty Management System
    </p>

</footer>


</body>

</html>