<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>

<%
    if (session == null || session.getAttribute("email") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String role = (String) session.getAttribute("role");

    if (role == null || !"HACKER".equalsIgnoreCase(role)) {
        response.sendRedirect("login.jsp");
        return;
    }

    String email = (String) session.getAttribute("email");

    Double totalReward =
        (Double) request.getAttribute("totalReward");

    Double pendingReward =
        (Double) request.getAttribute("pendingReward");

    Double paidReward =
        (Double) request.getAttribute("paidReward");

    if (totalReward == null) totalReward = 0.0;
    if (pendingReward == null) pendingReward = 0.0;
    if (paidReward == null) paidReward = 0.0;

    List<String[]> recentReports =
        (List<String[]>) request.getAttribute("recentReports");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Hacker Dashboard | Bug Bounty Management System</title>

    <link rel="stylesheet"
          href="css/style.css">

</head>

<body>

<!-- ================================
     NAVIGATION BAR
================================ -->

<nav class="navbar">

    <div class="nav-container">

        <div class="logo">
            Bug Bounty System
        </div>

        <div class="nav-links">

            <a href="dashboard" class="active">
                Dashboard
            </a>

            <a href="myReports">
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


<!-- ================================
     DASHBOARD HEADER
================================ -->

<section class="dashboard-header">

    <div class="dashboard-container">

        <h1>Welcome, Hacker! 👋</h1>

        <p>
            Track your bug reports, bounty rewards and submissions.
        </p>

        <p class="user-email">
            Logged in as:
            <strong><%= email %></strong>
        </p>

    </div>

</section>


<!-- ================================
     MAIN DASHBOARD
================================ -->

<main class="dashboard-container">


    <!-- ============================
         REWARD SUMMARY
    ============================= -->

    <div class="reward-summary">


        <!-- TOTAL BOUNTY -->

        <div class="reward-card total">

            <div class="reward-icon">
                💰
            </div>

            <div>

                <h3>
                    Total Bounty
                </h3>

                <p>
                    ₹<%= String.format("%.2f", totalReward) %>
                </p>

            </div>

        </div>


        <!-- PENDING REWARDS -->

        <div class="reward-card pending">

            <div class="reward-icon">
                ⏳
            </div>

            <div>

                <h3>
                    Pending Rewards
                </h3>

                <p>
                    ₹<%= String.format("%.2f", pendingReward) %>
                </p>

            </div>

        </div>


        <!-- PAID REWARDS -->

        <div class="reward-card paid">

            <div class="reward-icon">
                ✅
            </div>

            <div>

                <h3>
                    Paid Rewards
                </h3>

                <p>
                    ₹<%= String.format("%.2f", paidReward) %>
                </p>

            </div>

        </div>


    </div>


    <!-- ============================
         QUICK ACTIONS
    ============================= -->

    <div class="quick-actions">

        <a href="submit-bug.jsp"
           class="dashboard-action-btn">

            🐞 Submit New Bug

        </a>

        <a href="myReports"
           class="dashboard-action-btn secondary">

            📋 View My Reports

        </a>

    </div>


    <!-- ============================
         RECENT BUG REPORTS
    ============================= -->

    <div class="recent-reports-section">


        <div class="section-header">

            <div>

                <h2>
                    Recent Bug Reports
                </h2>

                <p>
                    Your latest 5 submitted reports
                </p>

            </div>


            <a href="myReports"
               class="view-all-btn">

                View All

            </a>

        </div>


        <% if (recentReports != null &&
               !recentReports.isEmpty()) { %>


            <div class="recent-reports-table-container">

                <table class="recent-reports-table">

                    <thead>

                        <tr>

                            <th>
                                ID
                            </th>

                            <th>
                                Bug Title
                            </th>

                            <th>
                                Severity
                            </th>

                            <th>
                                Status
                            </th>

                            <th>
                                Reward
                            </th>

                            <th>
                                Action
                            </th>

                        </tr>

                    </thead>


                    <tbody>


                    <% for (String[] report : recentReports) { %>

                        <tr>


                            <!-- BUG ID -->

                            <td>

                                #<%= report[0] %>

                            </td>


                            <!-- BUG TITLE -->

                            <td>

                                <strong>
                                    <%= report[1] %>
                                </strong>

                                <br>

                                <small>
                                    <%= report[2] %>
                                </small>

                            </td>


                            <!-- SEVERITY -->

                            <td>

                                <span class="status
                                    <%= report[3]
                                        .toLowerCase()
                                        .replace(" ", "-") %>">

                                    <%= report[3] %>

                                </span>

                            </td>


                            <!-- BUG STATUS -->

                            <td>

                                <span class="status
                                    <%= report[4]
                                        .toLowerCase()
                                        .replace(" ", "-") %>">

                                    <%= report[4] %>

                                </span>

                            </td>


                            <!-- REWARD -->

                            <td>

                                <strong>

                                    ₹<%= String.format(
                                        "%.2f",
                                        Double.parseDouble(report[5])
                                    ) %>

                                </strong>

                                <br>

                                <span class="reward-status
                                    <%= report[6].toLowerCase() %>">

                                    <%= report[6] %>

                                </span>

                            </td>


                            <!-- VIEW -->

                            <td>

                                <a href="myBugDetails?bugId=<%= report[0] %>"
                                   class="view-btn">

                                    View

                                </a>

                            </td>


                        </tr>

                    <% } %>


                    </tbody>

                </table>

            </div>


        <% } else { %>


            <!-- NO REPORTS -->

            <div class="no-reports">

                <div class="no-reports-icon">
                    🐞
                </div>

                <h3>
                    No Bug Reports Yet
                </h3>

                <p>
                    You haven't submitted any bug reports.
                </p>

                <a href="submit-bug.jsp"
                   class="btn">

                    Submit Your First Bug

                </a>

            </div>


        <% } %>


    </div>


</main>


<!-- ================================
     FOOTER
================================ -->

<footer class="footer">

    <p>
        © 2026 Bug Bounty Management System
    </p>

</footer>


</body>

</html>