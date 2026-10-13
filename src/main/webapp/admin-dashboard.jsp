<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.bugbounty.util.CsrfTokenUtil" %>

<%

    // =========================================

    // SESSION CHECK

    // =========================================

    if (session == null || session.getAttribute("email") == null) {

        response.sendRedirect("login.jsp");

        return;

    }

    String role = (String) session.getAttribute("role");

    if (role == null || !"ADMIN".equalsIgnoreCase(role)) {

        response.sendRedirect("dashboard.jsp");

        return;

    }

    String email = (String) session.getAttribute("email");
    String csrfToken = CsrfTokenUtil.getOrCreateToken(request);

    // =========================================

    // REPORT DATA

    // =========================================

    List<String[]> reports =

            (List<String[]>) request.getAttribute("reports");

    String search =

            (String) request.getAttribute("search");

    String selectedStatus =

            (String) request.getAttribute("selectedStatus");

    String selectedSeverity =

            (String) request.getAttribute("selectedSeverity");

    if (search == null) {

        search = "";

    }

    if (selectedStatus == null) {

        selectedStatus = "";

    }

    if (selectedSeverity == null) {

        selectedSeverity = "";

    }

    // =========================================

    // BUG STATISTICS

    // =========================================

    Integer totalReports =

            (Integer) request.getAttribute("totalReports");

    Integer submittedReports =

            (Integer) request.getAttribute("submittedReports");

    Integer underReviewReports =

            (Integer) request.getAttribute("underReviewReports");

    Integer verifiedReports =

            (Integer) request.getAttribute("verifiedReports");

    Integer resolvedReports =

            (Integer) request.getAttribute("resolvedReports");

    Integer rejectedReports =

            (Integer) request.getAttribute("rejectedReports");

    if (totalReports == null) {

        totalReports = 0;

    }

    if (submittedReports == null) {

        submittedReports = 0;

    }

    if (underReviewReports == null) {

        underReviewReports = 0;

    }

    if (verifiedReports == null) {

        verifiedReports = 0;

    }

    if (resolvedReports == null) {

        resolvedReports = 0;

    }

    if (rejectedReports == null) {

        rejectedReports = 0;

    }

    // =========================================

    // REWARD STATISTICS

    // =========================================

    Double totalBounty =

            (Double) request.getAttribute("totalBounty");

    Double pendingBounty =

            (Double) request.getAttribute("pendingBounty");

    Double paidBounty =

            (Double) request.getAttribute("paidBounty");

    if (totalBounty == null) {

        totalBounty = 0.0;

    }

    if (pendingBounty == null) {

        pendingBounty = 0.0;

    }

    if (paidBounty == null) {

        paidBounty = 0.0;

    }

    // =========================================

    // MESSAGES

    // =========================================

    String error =

            request.getParameter("error");

    String rewardMessage =

            request.getParameter("reward");

%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"

          content="width=device-width, initial-scale=1.0">

    <title>

        Admin Dashboard | Bug Bounty Management System

    </title>

    <link rel="stylesheet"

          href="css/style.css">

</head>

<body>

<!-- =========================================

     NAVIGATION

\========================================= -->

<nav class="navbar">

    <div class="nav-container">

        <div class="logo">

            Bug Bounty System

        </div>

        <div class="nav-links">

            <a href="admin" class="active">

                Dashboard

            </a>

            <a href="activityLogs">

                Activity Logs

            </a>

            <a href="logout">

                Logout

            </a>

        </div>

    </div>

</nav>

<!-- =========================================

     ADMIN HEADER

\========================================= -->

<section class="admin-header">

    <div class="admin-container">

        <h1>

            Admin Dashboard 🛡️

        </h1>

        <p>

            Manage bug reports, review vulnerabilities

            and assign bounty rewards.

        </p>

        <p class="admin-email">

            Logged in as:

            <strong>

                <%= email %>

            </strong>

        </p>

    </div>

</section>

<main class="admin-container">

<!-- =========================================

     SUCCESS / ERROR MESSAGES

\========================================= -->

<%

    if ("success".equals(error)) {

%>

    <div class="success-message">

        Bug status updated successfully.

    </div>

<%

    }

%>

<%

    if ("success".equals(rewardMessage)) {

%>

    <div class="success-message">

        Bounty reward updated successfully.

    </div>

<%

    }

%>

<%

    if ("notfound".equals(error)) {

%>

    <div class="error-message">

        Bug report not found.

    </div>

<%

    }

%>

<%

    if ("server".equals(error)) {

%>

    <div class="error-message">

        Something went wrong.

        Please try again.

    </div>

<%

    }

%>

<%

    if ("reward".equals(error)) {

%>

    <div class="error-message">

        Unable to update the bounty reward.

    </div>

<%

    }

%>

<!-- =========================================

     BUG REPORT STATISTICS

\========================================= -->

<div class="stats-grid">

    <!-- TOTAL -->

    <div class="stat-card">

        <div class="stat-icon">

            📊

        </div>

        <div>

            <h3>

                Total Reports

            </h3>

            <p>

                <%= totalReports %>

            </p>

        </div>

    </div>

    <!-- SUBMITTED -->

    <div class="stat-card">

        <div class="stat-icon">

            📥

        </div>

        <div>

            <h3>

                Submitted

            </h3>

            <p>

                <%= submittedReports %>

            </p>

        </div>

    </div>

    <!-- UNDER REVIEW -->

    <div class="stat-card">

        <div class="stat-icon">

            🔍

        </div>

        <div>

            <h3>

                Under Review

            </h3>

            <p>

                <%= underReviewReports %>

            </p>

        </div>

    </div>

    <!-- VERIFIED -->

    <div class="stat-card">

        <div class="stat-icon">

            ✔️

        </div>

        <div>

            <h3>

                Verified

            </h3>

            <p>

                <%= verifiedReports %>

            </p>

        </div>

    </div>

    <!-- RESOLVED -->

    <div class="stat-card">

        <div class="stat-icon">

            ✅

        </div>

        <div>

            <h3>

                Resolved

            </h3>

            <p>

                <%= resolvedReports %>

            </p>

        </div>

    </div>

    <!-- REJECTED -->

    <div class="stat-card">

        <div class="stat-icon">

            ❌

        </div>

        <div>

            <h3>

                Rejected

            </h3>

            <p>

                <%= rejectedReports %>

            </p>

        </div>

    </div>

</div>

<!-- =========================================

     BOUNTY STATISTICS

\========================================= -->

<div class="reward-summary">

    <!-- TOTAL BOUNTY -->

    <div class="reward-card total">

        <div class="reward-icon">

            💰

        </div>

        <div>

            <h3>

                Total Bounties

            </h3>

            <p>

                ₹<%= String.format("%.2f", totalBounty) %>

            </p>

        </div>

    </div>

    <!-- PENDING BOUNTY -->

    <div class="reward-card pending">

        <div class="reward-icon">

            ⏳

        </div>

        <div>

            <h3>

                Pending / Approved

            </h3>

            <p>

                ₹<%= String.format("%.2f", pendingBounty) %>

            </p>

        </div>

    </div>

    <!-- PAID BOUNTY -->

    <div class="reward-card paid">

        <div class="reward-icon">

            ✅

        </div>

        <div>

            <h3>

                Paid Bounties

            </h3>

            <p>

                ₹<%= String.format("%.2f", paidBounty) %>

            </p>

        </div>

    </div>

</div>

<!-- =========================================

     SEARCH & FILTERS

\========================================= -->

<div class="filter-container">

    <form action="admin"

          method="get"

          class="filter-form">

        <!-- SEARCH -->

        <div class="filter-group">

            <label for="search">

                Search

            </label>

            <input

                type="text"

                id="search"

                name="search"

                value="<%= search %>"

                placeholder="Bug title, type or hacker name">

        </div>

        <!-- STATUS -->

        <div class="filter-group">

            <label for="status">

                Bug Status

            </label>

            <select

                id="status"

                name="status">

                <option value="">

                    All Statuses

                </option>

                <option value="SUBMITTED"

                    <%= "SUBMITTED".equals(selectedStatus)

                        ? "selected"

                        : "" %>>

                    Submitted

                </option>

                <option value="UNDER REVIEW"

                    <%= "UNDER REVIEW".equals(selectedStatus)

                        ? "selected"

                        : "" %>>

                    Under Review

                </option>

                <option value="VERIFIED"

                    <%= "VERIFIED".equals(selectedStatus)

                        ? "selected"

                        : "" %>>

                    Verified

                </option>

                <option value="REJECTED"

                    <%= "REJECTED".equals(selectedStatus)

                        ? "selected"

                        : "" %>>

                    Rejected

                </option>

                <option value="RESOLVED"

                    <%= "RESOLVED".equals(selectedStatus)

                        ? "selected"

                        : "" %>>

                    Resolved

                </option>

            </select>

        </div>

        <!-- SEVERITY -->

        <div class="filter-group">

            <label for="severity">

                Severity

            </label>

            <select

                id="severity"

                name="severity">

                <option value="">

                    All Severities

                </option>

                <option value="LOW"

                    <%= "LOW".equals(selectedSeverity)

                        ? "selected"

                        : "" %>>

                    Low

                </option>

                <option value="MEDIUM"

                    <%= "MEDIUM".equals(selectedSeverity)

                        ? "selected"

                        : "" %>>

                    Medium

                </option>

                <option value="HIGH"

                    <%= "HIGH".equals(selectedSeverity)

                        ? "selected"

                        : "" %>>

                    High

                </option>

                <option value="CRITICAL"

                    <%= "CRITICAL".equals(selectedSeverity)

                        ? "selected"

                        : "" %>>

                    Critical

                </option>

            </select>

        </div>

        <!-- BUTTONS -->

        <div class="filter-actions">

            <button

                type="submit"

                class="btn">

                Search

            </button>

            <a

                href="admin"

                class="btn secondary-btn">

                Reset

            </a>

        </div>

    </form>

</div>

<!-- =========================================

     BUG REPORTS

\========================================= -->

<div class="admin-table-container">

    <div class="table-header">

        <div>

            <h2>

                Bug Reports

            </h2>

            <p>

                Review vulnerabilities and manage

                bounty rewards.

            </p>

        </div>

    </div>

<%

    if (reports != null && !reports.isEmpty()) {

%>

    <div class="table-responsive">

        <table class="admin-table">

            <thead>

                <tr>

                    <th>

                        ID

                    </th>

                    <th>

                        Hacker

                    </th>

                    <th>

                        Bug Title

                    </th>

                    <th>

                        Type

                    </th>

                    <th>

                        Severity

                    </th>

                    <th>

                        Bug Status

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

<%

    for (String[] report : reports) {

%>

                <tr>

                    <!-- ID -->

                    <td>

                        \#<%= report[0] %>

                    </td>

                    <!-- HACKER -->

                    <td>

                        <strong>

                            <%= report[1] %>

                        </strong>

                    </td>

                    <!-- TITLE -->

                    <td>

                        <strong>

                            <%= report[2] %>

                        </strong>

                    </td>

                    <!-- TYPE -->

                    <td>

                        <%= report[3] %>

                    </td>

                    <!-- SEVERITY -->

                    <td>

                        <span class="status

                            <%= report[4]

                                .toLowerCase()

                                .replace(" ", "-") %>">

                            <%= report[4] %>

                        </span>

                    </td>

                    <!-- BUG STATUS -->

                    <td>

                        <span class="status

                            <%= report[5]

                                .toLowerCase()

                                .replace(" ", "-") %>">

                            <%= report[5] %>

                        </span>

                    </td>

                    <!-- BOUNTY -->

                    <td>

                        <span class="reward-amount">

                            ₹<%= String.format(

                                "%.2f",

                                Double.parseDouble(report[7])

                            ) %>

                        </span>

                    </td>

                    <!-- REWARD STATUS -->

                    <td>

                        <span class="reward-status

                            <%= report[8].toLowerCase() %>">

                            <%= report[8] %>

                        </span>

                    </td>

                    <!-- SUBMITTED -->

                    <td>

                        <%= report[6] %>

                    </td>

                    <!-- ACTION -->

                    <td>

                        <div class="admin-actions">

                            <!-- VIEW -->

                            <a

                                href="bugDetails?bugId=<%= report[0] %>"

                                class="view-btn">

                                View

                            </a>

                            <!-- STATUS UPDATE -->

                            <form

                                action="updateBugStatus"

                                method="post"

                                class="status-form">

                                <input
                                    type="hidden"
                                    name="csrfToken"
                                    value="<%= csrfToken %>">

                                <input

                                    type="hidden"

                                    name="bugId"

                                    value="<%= report[0] %>">

                                <select

                                    name="status">

                                    <option value="SUBMITTED"

                                        <%= "SUBMITTED".equals(report[5])

                                            ? "selected"

                                            : "" %>>

                                        Submitted

                                    </option>

                                    <option value="UNDER REVIEW"

                                        <%= "UNDER REVIEW".equals(report[5])

                                            ? "selected"

                                            : "" %>>

                                        Under Review

                                    </option>

                                    <option value="VERIFIED"

                                        <%= "VERIFIED".equals(report[5])

                                            ? "selected"

                                            : "" %>>

                                        Verified

                                    </option>

                                    <option value="REJECTED"

                                        <%= "REJECTED".equals(report[5])

                                            ? "selected"

                                            : "" %>>

                                        Rejected

                                    </option>

                                    <option value="RESOLVED"

                                        <%= "RESOLVED".equals(report[5])

                                            ? "selected"

                                            : "" %>>

                                        Resolved

                                    </option>

                                </select>

                                <button

                                    type="submit"

                                    class="update-btn">

                                    Update

                                </button>

                            </form>

                        </div>

                    </td>

                </tr>

<%

    }

%>

            </tbody>

        </table>

    </div>

<%

    } else {

%>

    <!-- NO REPORTS -->

    <div class="no-reports">

        <div class="no-reports-icon">

            🐞

        </div>

        <h3>

            No Bug Reports Found

        </h3>

        <p>

            No reports match the selected

            search or filters.

        </p>

    </div>

<%

    }

%>

</div>

</main>

<!-- =========================================

     FOOTER

\========================================= -->

<footer class="footer">

    <p>

        © 2026 Bug Bounty Management System

    </p>

</footer>

</body>

</html>