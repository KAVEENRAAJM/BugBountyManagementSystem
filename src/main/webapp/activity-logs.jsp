<%@ page import="java.util.List" %>
<%@ page import="com.bugbounty.dao.ActivityLogDAO.ActivityLog" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    HttpSession sessionObj = request.getSession(false);

    if (sessionObj == null ||
            sessionObj.getAttribute("userId") == null ||
            sessionObj.getAttribute("email") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String role = String.valueOf(sessionObj.getAttribute("role"));

    if (!"ADMIN".equalsIgnoreCase(role)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }

    List<ActivityLog> logs =
            (List<ActivityLog>) request.getAttribute("activityLogs");

    String selectedAction =
            (String) request.getAttribute("selectedAction");

    String searchText =
            (String) request.getAttribute("searchText");

    if (logs == null) {
        logs = java.util.Collections.emptyList();
    }

    if (selectedAction == null || selectedAction.trim().isEmpty()) {
        selectedAction = "ALL";
    }

    if (searchText == null) {
        searchText = "";
    }

    // Escape user/database text before rendering it in HTML.
    java.util.function.Function<String, String> escapeHtml = value -> {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    };

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Activity Logs - BugBounty</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        .activity-page {
            max-width: 1450px;
            width: 94%;
            margin: 35px auto;
        }

        .activity-header,
        .activity-card {
            background: #fff;
            border-radius: 16px;
            padding: 24px;
            margin-bottom: 24px;
            box-shadow: 0 8px 28px rgba(20, 30, 60, 0.07);
        }

        .activity-header h1 {
            margin: 0 0 8px;
        }

        .activity-header p {
            color: #64748b;
            margin-bottom: 0;
        }

        .activity-summary {
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
            margin-top: 22px;
        }

        .summary-item {
            background: #f5f3ff;
            color: #4c1d95;
            padding: 12px 16px;
            border-radius: 10px;
        }

        .filter-form {
            display: flex;
            gap: 12px;
            align-items: end;
            flex-wrap: wrap;
        }

        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 7px;
            flex: 1;
            min-width: 200px;
        }

        .filter-group label {
            font-weight: 600;
            font-size: 14px;
        }

        .filter-group input,
        .filter-group select {
            width: 100%;
            min-height: 43px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            padding: 9px 12px;
            background: #fff;
        }

        .activity-table-wrapper {
            overflow-x: auto;
            margin-top: 18px;
        }

        .activity-table {
            width: 100%;
            border-collapse: collapse;
            min-width: 850px;
        }

        .activity-table th,
        .activity-table td {
            text-align: left;
            padding: 14px 12px;
            border-bottom: 1px solid #e5e7eb;
            vertical-align: top;
            overflow-wrap: anywhere;
        }

        .activity-table th {
            background: #f8fafc;
            color: #475569;
            font-size: 13px;
            text-transform: uppercase;
        }

        .activity-table tbody tr:hover {
            background: #fafafa;
        }

        .action-badge {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 20px;
            background: #ede9fe;
            color: #5b21b6;
            font-size: 12px;
            font-weight: 700;
            white-space: nowrap;
        }

        .activity-description {
            max-width: 360px;
            white-space: pre-wrap;
        }

        .activity-muted {
            color: #64748b;
        }

        .activity-empty {
            text-align: center;
            padding: 35px 15px;
            color: #64748b;
        }

        .activity-actions {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-top: 20px;
        }

        .activity-actions a {
            text-decoration: none;
        }

        @media (max-width: 600px) {
            .activity-page {
                width: 96%;
                margin: 18px auto;
            }

            .activity-header,
            .activity-card {
                padding: 16px;
            }
        }
    </style>
</head>

<body>

<nav class="navbar">
    <div class="navbar-container">
        <div class="logo">BugBounty</div>

        <div class="nav-links">
            <a href="<%= contextPath %>/admin">Dashboard</a>
            <a href="<%= contextPath %>/activityLogs" class="active">
                Activity Logs
            </a>
            <a href="<%= contextPath %>/logout">Logout</a>
        </div>
    </div>
</nav>

<main class="activity-page">

    <section class="activity-header">
        <h1>Activity Logs</h1>
        <p>
            Review recorded actions across bug reports, rewards,
            and evidence uploads.
        </p>

        <div class="activity-summary">
            <div class="summary-item">
                <strong><%= logs.size() %></strong>
                matching record(s)
            </div>

            <div class="summary-item">
                <strong>500</strong>
                maximum records per query
            </div>
        </div>
    </section>

    <section class="activity-card">
        <h2>Search and Filter</h2>

        <form
            class="filter-form"
            action="<%= contextPath %>/activityLogs"
            method="get">

            <div class="filter-group">
                <label for="search">Search activity</label>

                <input
                    type="search"
                    id="search"
                    name="search"
                    maxlength="200"
                    placeholder="Search descriptions or actions"
                    value="<%= escapeHtml.apply(searchText) %>">
            </div>

            <div class="filter-group">
                <label for="action">Activity type</label>

                <select id="action" name="action">

                    <option value="ALL"
                        <%= "ALL".equalsIgnoreCase(selectedAction)
                                ? "selected" : "" %>>
                        All Activities
                    </option>

                    <option value="BUG_SUBMITTED"
                        <%= "BUG_SUBMITTED".equalsIgnoreCase(selectedAction)
                                ? "selected" : "" %>>
                        Bug Submitted
                    </option>

                    <option value="BUG_STATUS_UPDATED"
                        <%= "BUG_STATUS_UPDATED".equalsIgnoreCase(selectedAction)
                                ? "selected" : "" %>>
                        Bug Status Updated
                    </option>

                    <option value="REWARD_CREATED"
                        <%= "REWARD_CREATED".equalsIgnoreCase(selectedAction)
                                ? "selected" : "" %>>
                        Reward Created
                    </option>

                    <option value="REWARD_UPDATED"
                        <%= "REWARD_UPDATED".equalsIgnoreCase(selectedAction)
                                ? "selected" : "" %>>
                        Reward Updated
                    </option>

                    <option value="EVIDENCE_UPLOADED"
                        <%= "EVIDENCE_UPLOADED".equalsIgnoreCase(selectedAction)
                                ? "selected" : "" %>>
                        Evidence Uploaded
                    </option>

                </select>
            </div>

            <button type="submit" class="btn">
                Search
            </button>

            <a href="<%= contextPath %>/activityLogs"
               class="btn secondary">
                Clear
            </a>

        </form>
    </section>

    <section class="activity-card">
        <h2>Recorded Activities</h2>

        <div class="activity-table-wrapper">
            <table class="activity-table">

                <thead>
                    <tr>
                        <th>Log ID</th>
                        <th>User ID</th>
                        <th>Action</th>
                        <th>Description</th>
                        <th>Bug ID</th>
                        <th>Created At</th>
                    </tr>
                </thead>

                <tbody>

                <% if (logs.isEmpty()) { %>

                    <tr>
                        <td colspan="6" class="activity-empty">
                            No activity records found for these filters.
                        </td>
                    </tr>

                <% } else { %>

                    <% for (ActivityLog log : logs) { %>

                        <tr>
                            <td>
                                #<%= log.getLogId() %>
                            </td>

                            <td>
                                <%= log.getUserId() != null
                                        ? log.getUserId()
                                        : "System / Unknown" %>
                            </td>

                            <td>
                                <span class="action-badge">
                                    <%= escapeHtml.apply(log.getAction()) %>
                                </span>
                            </td>

                            <td class="activity-description">
                                <%= escapeHtml.apply(log.getDescription()) %>
                            </td>

                            <td>
                                <% if (log.getBugId() != null) { %>
                                    <a href="<%= contextPath %>/bugDetails?bugId=<%= log.getBugId() %>">
                                        #<%= log.getBugId() %>
                                    </a>
                                <% } else { %>
                                    <span class="activity-muted">Not linked</span>
                                <% } %>
                            </td>

                            <td>
                                <%= escapeHtml.apply(log.getCreatedAt()) %>
                            </td>
                        </tr>

                    <% } %>

                <% } %>

                </tbody>
            </table>
        </div>

        <div class="activity-actions">
            <a href="<%= contextPath %>/admin" class="btn secondary">
                ← Back to Dashboard
            </a>
        </div>

    </section>

</main>

<footer>
    <p>© 2026 BugBounty Management System</p>
</footer>

</body>
</html>
