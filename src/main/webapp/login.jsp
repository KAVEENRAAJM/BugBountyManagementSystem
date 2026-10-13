<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Login - BugBounty</title>

    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<nav class="navbar">

    <div class="logo">
        BugBounty
    </div>

    <div class="nav-links">
        <a href="index.jsp">Home</a>
        <a href="register.jsp">Register</a>
    </div>

</nav>

<div class="form-container">

    <h1>Welcome Back</h1>

    <p>Login to your BugBounty account</p>

    <%
        String error = request.getParameter("error");

        if ("invalid".equals(error)) {
    %>

        <p style="color: red;">
            Invalid email or password.
        </p>

    <%
        }

        String success = request.getParameter("success");

        if ("registered".equals(success)) {
    %>

        <p style="color: green;">
            Registration successful. Please login.
        </p>

    <%
        }
    %>

    <form action="login" method="post">

        <label>Email</label>

        <input
            type="email"
            name="email"
            required
        >

        <label>Password</label>

        <input
            type="password"
            name="password"
            required
        >

        <button type="submit" class="form-button">
            Login
        </button>

    </form>

    <p>
        Don't have an account?
        <a href="register.jsp">Register</a>
    </p>

</div>

</body>

</html>