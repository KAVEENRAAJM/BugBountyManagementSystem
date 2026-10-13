
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="com.bugbounty.util.CsrfTokenUtil" %>

<%
    HttpSession userSession = request.getSession(false);

    if (userSession == null ||
        userSession.getAttribute("email") == null ||
        userSession.getAttribute("userId") == null ||
        !"HACKER".equalsIgnoreCase(
            String.valueOf(userSession.getAttribute("role")))) {

        response.sendRedirect("login.jsp");
        return;
    }

    String csrfToken = CsrfTokenUtil.getOrCreateToken(request);
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Submit Bug - BugBounty</title>

    <link rel="stylesheet"
          href="css/style.css">
</head>

<body>

<nav class="navbar">

    <div class="logo">
        BugBounty
    </div>

    <div class="nav-links">
        <a href="dashboard.jsp">Dashboard</a>
        <a href="submit-bug.jsp">Submit Bug</a>
        <a href="logout">Logout</a>
    </div>

</nav>

<div class="form-container bug-form">

    <h1>Submit Vulnerability</h1>

    <p>
        Report a security vulnerability to the organization.
    </p>

    <form action="submitBug" method="post">

        <!-- CSRF protection token -->
        <input
            type="hidden"
            name="csrfToken"
            value="<%= csrfToken %>"
        >

        <label>Bug Title</label>

        <input
            type="text"
            name="title"
            placeholder="Example: SQL Injection in Login"
            required
        >

        <label>Vulnerability Type</label>

        <select name="vulnerabilityType" required>

            <option value="">Select vulnerability type</option>

            <option value="SQL Injection">SQL Injection</option>

            <option value="XSS">Cross-Site Scripting (XSS)</option>

            <option value="CSRF">Cross-Site Request Forgery (CSRF)</option>

            <option value="Authentication">Authentication Bypass</option>

            <option value="Authorization">Broken Access Control</option>

            <option value="IDOR">IDOR</option>

            <option value="Other">Other</option>

        </select>

        <label>Severity</label>

        <select name="severity" required>

            <option value="">Select severity</option>

            <option value="LOW">Low</option>

            <option value="MEDIUM">Medium</option>

            <option value="HIGH">High</option>

            <option value="CRITICAL">Critical</option>

        </select>

        <label>Affected URL</label>

        <input
            type="url"
            name="affectedUrl"
            placeholder="https://example.com/login"
        >

        <label>Description</label>

        <textarea
            name="description"
            rows="6"
            placeholder="Describe the vulnerability..."
            required
        ></textarea>

        <label>Steps to Reproduce</label>

        <textarea
            name="steps"
            rows="6"
            placeholder="1. Open login page&#10;2. Enter malicious input&#10;3. Observe the result..."
        ></textarea>

        <label>Evidence</label>

        <textarea
            name="evidence"
            rows="5"
            placeholder="Describe screenshots, payloads, or other evidence..."
        ></textarea>

        <button
            type="submit"
            class="form-button">

            Submit Bug Report

        </button>

    </form>

</div>

</body>
</html>
