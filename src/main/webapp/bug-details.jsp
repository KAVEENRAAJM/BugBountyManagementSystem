
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="jakarta.servlet.http.HttpServletResponse" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bugbounty.dao.EvidenceDAO.Evidence" %>
<%@ page import="com.bugbounty.util.CsrfTokenUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    HttpSession sessionObj = request.getSession(false);

    if (sessionObj == null ||
            sessionObj.getAttribute("email") == null) {

        response.sendRedirect("login.jsp");
        return;
    }

    String role = (String) sessionObj.getAttribute("role");

    if (role == null || !"ADMIN".equalsIgnoreCase(role)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }

    // Generate or retrieve the CSRF token for this session.
    String csrfToken = CsrfTokenUtil.getOrCreateToken(request);

    String[] bug = (String[]) request.getAttribute("bug");

    if (bug == null) {
        response.sendRedirect("admin?error=notfound");
        return;
    }

    String rewardError = request.getParameter("error");
    String rewardMessage = request.getParameter("reward");

    String[] reward =
            (String[]) request.getAttribute("rewardDetails");

    List<Evidence> evidenceList =
            (List<Evidence>) request.getAttribute("evidenceList");

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Bug Details - BugBounty</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        .evidence-table-wrapper {
            width: 100%;
            overflow-x: auto;
            margin-top: 18px;
        }

        .evidence-table {
            width: 100%;
            border-collapse: collapse;
        }

        .evidence-table th,
        .evidence-table td {
            padding: 12px;
            border-bottom: 1px solid #e5e7eb;
            text-align: left;
            overflow-wrap: anywhere;
        }

        .evidence-table th {
            font-weight: 700;
        }

        .evidence-download {
            display: inline-block;
            padding: 8px 12px;
            border-radius: 6px;
            background: #ede9fe;
            color: #5b21b6;
            text-decoration: none;
            font-weight: 600;
            white-space: nowrap;
        }

        .evidence-download:hover {
            background: #ddd6fe;
        }

        .evidence-help {
            color: #6b7280;
            font-size: 14px;
            line-height: 1.6;
        }

        .evidence-count {
            display: inline-block;
            padding: 4px 10px;
            margin-left: 8px;
            border-radius: 20px;
            background: #ede9fe;
            color: #5b21b6;
            font-size: 13px;
        }

        .content-box,
        .code-box {
            overflow-wrap: anywhere;
            white-space: pre-wrap;
        }
    </style>
</head>

<body>

<!-- NAVBAR -->
<nav class="navbar">
    <div class="navbar-container">

        <div class="logo">BugBounty</div>

        <div class="nav-links">
            <a href="admin">Dashboard</a>
            <a href="admin">Reports</a>
            <a href="logout">Logout</a>
        </div>

    </div>
</nav>

<!-- MAIN CONTENT -->
<div class="bug-details-container">

    <!-- HEADER -->
    <div class="details-header">

        <div>
            <p class="details-label">BUG REPORT</p>

            <h1><%= bug[3] %></h1>

            <p>
                Bug ID:
                <strong>#<%= bug[0] %></strong>
            </p>
        </div>

        <div class="bug-status">
            <span><%= bug[10] %></span>
        </div>

    </div>

    <!-- REWARD SUCCESS MESSAGE -->
    <% if ("success".equals(rewardMessage)) { %>
        <div class="success-message" role="status">
            Reward updated successfully.
        </div>
    <% } %>

    <!-- REWARD ERROR MESSAGE -->
    <% if ("reward".equals(rewardError)) { %>
        <div class="error-message" role="alert">
            Failed to update reward. Please check the amount and status.
        </div>
    <% } %>

    <div class="details-grid">

        <!-- HACKER INFORMATION -->
        <div class="details-card">
            <h2>Hacker Information</h2>

            <div class="detail-item">
                <span>Name</span>
                <strong><%= bug[1] %></strong>
            </div>

            <div class="detail-item">
                <span>Email</span>
                <strong><%= bug[2] %></strong>
            </div>
        </div>

        <!-- VULNERABILITY INFORMATION -->
        <div class="details-card">
            <h2>Vulnerability Information</h2>

            <div class="detail-item">
                <span>Type</span>
                <strong><%= bug[4] %></strong>
            </div>

            <div class="detail-item">
                <span>Severity</span>
                <strong><%= bug[6] %></strong>
            </div>

            <div class="detail-item">
                <span>Status</span>
                <strong><%= bug[10] %></strong>
            </div>
        </div>

        <!-- AFFECTED URL -->
        <div class="details-card full-width">
            <h2>Affected URL</h2>

            <div class="code-box"><%= bug[7] != null && !bug[7].trim().isEmpty()
                    ? bug[7]
                    : "Not provided" %></div>
        </div>

        <!-- DESCRIPTION -->
        <div class="details-card full-width">
            <h2>Description</h2>

            <div class="content-box"><%= bug[5] != null
                    ? bug[5]
                    : "No description provided" %></div>
        </div>

        <!-- STEPS TO REPRODUCE -->
        <div class="details-card full-width">
            <h2>Steps to Reproduce</h2>

            <div class="content-box"><%= bug[8] != null && !bug[8].trim().isEmpty()
                    ? bug[8]
                    : "Not provided" %></div>
        </div>

        <!-- EXISTING EVIDENCE DESCRIPTION -->
        <div class="details-card full-width">
            <h2>Existing Evidence Information</h2>

            <div class="content-box"><%= bug[9] != null && !bug[9].trim().isEmpty()
                    ? bug[9]
                    : "No previous evidence description provided" %></div>
        </div>

        <!-- UPLOADED EVIDENCE FILES -->
        <div class="details-card full-width">
            <h2>
                📎 Uploaded Evidence Files

                <span class="evidence-count">
                    <%= evidenceList != null ? evidenceList.size() : 0 %>
                </span>
            </h2>

            <p>
                Evidence files uploaded by the hacker for this report.
            </p>

            <% if (evidenceList != null && !evidenceList.isEmpty()) { %>

                <div class="evidence-table-wrapper">
                    <table class="evidence-table">

                        <thead>
                            <tr>
                                <th>Filename</th>
                                <th>File Type</th>
                                <th>Size</th>
                                <th>Uploaded At</th>
                                <th>Action</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% for (Evidence evidence : evidenceList) { %>
                                <tr>

                                    <td>
                                        <%= evidence.getOriginalFilename() %>
                                    </td>

                                    <td>
                                        <%= evidence.getContentType() %>
                                    </td>

                                    <td>
                                        <%= String.format(
                                                java.util.Locale.ROOT,
                                                "%.2f KB",
                                                evidence.getFileSize() / 1024.0
                                        ) %>
                                    </td>

                                    <td>
                                        <%= evidence.getUploadedAt() != null
                                                ? evidence.getUploadedAt()
                                                : "Not available" %>
                                    </td>

                                    <td>
                                        <a
                                            class="evidence-download"
                                            href="<%= contextPath %>/downloadEvidence?evidenceId=<%= evidence.getEvidenceId() %>">
                                            ⬇ Download
                                        </a>
                                    </td>

                                </tr>
                            <% } %>
                        </tbody>

                    </table>
                </div>

            <% } else { %>

                <p class="evidence-help">
                    No uploaded evidence files are registered for this report.
                </p>

                <p class="evidence-help">
                    If the hacker previously uploaded a file, check the
                    bug_evidence database table and the server's evidence
                    storage directory.
                </p>

            <% } %>
        </div>

        <!-- REWARD MANAGEMENT -->
        <div class="details-card full-width reward-card">

            <h2>💰 Bounty / Reward Management</h2>

            <p class="reward-description">
                Assign or update the bounty amount for this vulnerability report.
            </p>

            <form
                action="<%= contextPath %>/manageReward"
                method="post"
                class="reward-form">

                <!-- CSRF TOKEN -->
                <input
                    type="hidden"
                    name="csrfToken"
                    value="<%= csrfToken %>">

                <!-- BUG ID -->
                <input
                    type="hidden"
                    name="bugId"
                    value="<%= bug[0] %>">

                <!-- AMOUNT -->
                <div class="reward-form-group">
                    <label for="amount">Bounty Amount (₹)</label>

                    <input
                        type="number"
                        id="amount"
                        name="amount"
                        min="0"
                        step="0.01"
                        placeholder="Enter bounty amount"
                        value="<%= reward != null && reward[2] != null
                                ? reward[2]
                                : "" %>"
                        required>
                </div>

                <!-- REWARD STATUS -->
                <div class="reward-form-group">
                    <label for="rewardStatus">Reward Status</label>

                    <select id="rewardStatus" name="rewardStatus" required>

                        <option
                            value="PENDING"
                            <%= reward != null &&
                                    "PENDING".equalsIgnoreCase(reward[3])
                                    ? "selected"
                                    : "" %>>
                            Pending
                        </option>

                        <option
                            value="APPROVED"
                            <%= reward != null &&
                                    "APPROVED".equalsIgnoreCase(reward[3])
                                    ? "selected"
                                    : "" %>>
                            Approved
                        </option>

                        <option
                            value="PAID"
                            <%= reward != null &&
                                    "PAID".equalsIgnoreCase(reward[3])
                                    ? "selected"
                                    : "" %>>
                            Paid
                        </option>

                    </select>
                </div>

                <div class="reward-form-actions">
                    <button type="submit" class="btn">
                        Save Reward
                    </button>
                </div>

            </form>

            <!-- CURRENT REWARD -->
            <% if (reward != null) { %>

                <div class="existing-reward">

                    <div>
                        <span>Current Amount</span>
                        <strong>₹<%= reward[2] %></strong>
                    </div>

                    <div>
                        <span>Status</span>
                        <strong><%= reward[3] %></strong>
                    </div>

                    <div>
                        <span>Awarded At</span>
                        <strong>
                            <%= reward[4] != null
                                    ? reward[4]
                                    : "Not awarded yet" %>
                        </strong>
                    </div>

                </div>

            <% } %>

        </div>

    </div>

    <!-- ACTIONS -->
    <div class="details-actions">

        <a href="<%= contextPath %>/admin" class="btn secondary">
            ← Back to Admin Dashboard
        </a>

    </div>

</div>

<footer>
    <p>© 2026 BugBounty Management System</p>
</footer>

</body>
</html>
