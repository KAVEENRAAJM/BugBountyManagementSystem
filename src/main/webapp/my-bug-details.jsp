<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bugbounty.dao.EvidenceDAO.Evidence" %>
<%@ page import="com.bugbounty.util.CsrfTokenUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    HttpSession sessionObj = request.getSession(false);

    if (sessionObj == null ||
            sessionObj.getAttribute("userId") == null ||
            !"HACKER".equalsIgnoreCase(
                    String.valueOf(sessionObj.getAttribute("role")))) {
        response.sendRedirect("login.jsp");
        return;
    }

    String[] bug = (String[]) request.getAttribute("myBug");

    if (bug == null) {
        response.sendRedirect("myReports");
        return;
    }

    List<Evidence> evidenceList =
            (List<Evidence>) request.getAttribute("evidenceList");

    String evidenceMessage = request.getParameter("evidence");
    String errorMessage = request.getParameter("error");

    String uploadError = null;

    if ("file".equals(errorMessage)) {
        uploadError = "Upload could not be processed. Choose a file smaller than 5 MB.";
    } else if ("empty".equals(errorMessage)) {
        uploadError = "Please select a file before uploading.";
    } else if ("filesize".equals(errorMessage)) {
        uploadError = "File size must not exceed 5 MB.";
    } else if ("filename".equals(errorMessage)) {
        uploadError = "Invalid filename. Rename the file and try again.";
    } else if ("filetype".equals(errorMessage)) {
        uploadError = "Invalid file type or file contents. Use a genuine PNG, JPG, JPEG, or PDF.";
    } else if ("save".equals(errorMessage)) {
        uploadError = "Evidence could not be saved. Please try again.";
    }

    String csrfToken = CsrfTokenUtil.getOrCreateToken(request);
    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bug Details - BugBounty</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        .evidence-upload-card {
            margin-top: 24px;
        }

        .evidence-upload-form {
            display: flex;
            flex-direction: column;
            gap: 14px;
            margin-top: 18px;
        }

        .evidence-upload-form input[type="file"] {
            display: block;
            width: 100%;
            max-width: 100%;
            padding: 12px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            background: #fff;
            box-sizing: border-box;
        }

        .evidence-help {
            color: #6b7280;
            font-size: 14px;
            line-height: 1.6;
        }

        .evidence-message {
            padding: 12px 15px;
            border-radius: 8px;
            margin: 14px 0;
            line-height: 1.5;
        }

        .evidence-success {
            background: #dcfce7;
            color: #166534;
            border: 1px solid #86efac;
        }

        .evidence-error {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }

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

        .content-box {
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }

        .code-box {
            overflow-wrap: anywhere;
        }

        .upload-button {
            align-self: flex-start;
            cursor: pointer;
        }
    </style>
</head>

<body>

<nav class="navbar">
    <div class="navbar-container">
        <div class="logo">BugBounty</div>

        <div class="nav-links">
            <a href="dashboard.jsp">Dashboard</a>
            <a href="myReports" class="active">My Reports</a>
            <a href="submit-bug.jsp">Submit Bug</a>
            <a href="logout">Logout</a>
        </div>
    </div>
</nav>

<div class="bug-details-container">

    <!-- BUG HEADER -->
    <div class="details-header">
        <div>
            <p class="details-label">MY BUG REPORT</p>
            <h1><%= bug[1] %></h1>
            <p>
                Bug ID:
                <strong>#<%= bug[0] %></strong>
            </p>
        </div>

        <div class="bug-status">
            <span><%= bug[8] %></span>
        </div>
    </div>

    <div class="details-grid">

        <!-- VULNERABILITY INFORMATION -->
        <div class="details-card">
            <h2>Vulnerability Information</h2>

            <div class="detail-item">
                <span>Type</span>
                <strong><%= bug[2] %></strong>
            </div>

            <div class="detail-item">
                <span>Severity</span>
                <strong><%= bug[4] %></strong>
            </div>

            <div class="detail-item">
                <span>Bug Status</span>
                <strong><%= bug[8] %></strong>
            </div>
        </div>

        <!-- BOUNTY INFORMATION -->
        <div class="details-card reward-details-card">
            <h2>💰 Bounty Information</h2>

            <div class="detail-item">
                <span>Bounty Amount</span>
                <strong class="reward-amount">
                    ₹<%= bug[10] %>
                </strong>
            </div>

            <div class="detail-item">
                <span>Reward Status</span>
                <strong>
                    <span class="reward-status <%= bug[11].toLowerCase() %>">
                        <%= bug[11] %>
                    </span>
                </strong>
            </div>

            <div class="detail-item">
                <span>Awarded At</span>
                <strong>
                    <%= bug[12] != null ? bug[12] : "Not awarded yet" %>
                </strong>
            </div>
        </div>

        <!-- AFFECTED URL -->
        <div class="details-card full-width">
            <h2>Affected URL</h2>

            <div class="code-box">
                <%= bug[5] != null && !bug[5].trim().isEmpty()
                    ? bug[5]
                    : "Not provided" %>
            </div>
        </div>

        <!-- DESCRIPTION -->
        <div class="details-card full-width">
            <h2>Description</h2>

            <div class="content-box">
                <%= bug[3] != null ? bug[3] : "No description provided" %>
            </div>
        </div>

        <!-- STEPS TO REPRODUCE -->
        <div class="details-card full-width">
            <h2>Steps to Reproduce</h2>

            <div class="content-box">
                <%= bug[6] != null && !bug[6].trim().isEmpty()
                    ? bug[6]
                    : "Not provided" %>
            </div>
        </div>

        <!-- LEGACY EVIDENCE TEXT -->
        <div class="details-card full-width">
            <h2>Existing Evidence Information</h2>

            <div class="content-box">
                <%= bug[7] != null && !bug[7].trim().isEmpty()
                    ? bug[7]
                    : "No previous evidence description provided" %>
            </div>
        </div>

        <!-- UPLOADED EVIDENCE LIST -->
        <div class="details-card full-width">
            <h2>📂 Uploaded Evidence Files</h2>

            <% if (evidenceList != null && !evidenceList.isEmpty()) { %>

                <div class="evidence-table-wrapper">
                    <table class="evidence-table">
                        <thead>
                            <tr>
                                <th>Filename</th>
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
                    No uploaded evidence files found for this report yet.
                </p>

            <% } %>
        </div>

        <!-- UPLOAD EVIDENCE -->
        <div class="details-card full-width evidence-upload-card">
            <h2>📎 Upload Bug Evidence</h2>

            <p>
                Attach a screenshot or PDF that helps explain and reproduce
                the vulnerability in this report.
            </p>

            <% if ("success".equals(evidenceMessage)) { %>
                <div class="evidence-message evidence-success" role="status">
                    Evidence uploaded successfully!
                </div>
            <% } %>

            <% if (uploadError != null) { %>
                <div class="evidence-message evidence-error" role="alert">
                    <%= uploadError %>
                </div>
            <% } %>

            <form
                class="evidence-upload-form"
                action="<%= contextPath %>/uploadEvidence"
                method="post"
                enctype="multipart/form-data">

                <input
                    type="hidden"
                    name="bugId"
                    value="<%= bug[0] %>">

                <input
                    type="hidden"
                    name="csrfToken"
                    value="<%= csrfToken %>">

                <label for="evidenceFile">Select evidence file</label>

                <input
                    type="file"
                    id="evidenceFile"
                    name="evidenceFile"
                    accept=".png,.jpg,.jpeg,.pdf,image/png,image/jpeg,application/pdf"
                    required>

                <p class="evidence-help">
                    Accepted formats: PNG, JPG, JPEG, PDF.
                    Maximum file size: 5 MB.
                </p>

                <button type="submit" class="btn upload-button">
                    Upload Evidence
                </button>
            </form>

            <p class="evidence-help">
                Upload only evidence you are authorized to share.
                Files are stored outside the public web directory.
            </p>
        </div>

        <!-- SUBMISSION INFORMATION -->
        <div class="details-card full-width">
            <h2>Submission Information</h2>

            <div class="detail-item">
                <span>Submitted At</span>
                <strong><%= bug[9] %></strong>
            </div>
        </div>

    </div>

    <!-- ACTIONS -->
    <div class="details-actions">
        <a href="myReports" class="btn secondary">
            ← Back to My Reports
        </a>

        <a href="submit-bug.jsp" class="btn">
            + Submit New Bug
        </a>
    </div>

</div>

<footer>
    <p>© 2026 BugBounty Management System</p>
</footer>

</body>
</html>