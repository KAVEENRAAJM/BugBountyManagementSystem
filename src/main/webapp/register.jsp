<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Register - BugBounty</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<nav class="navbar">

    <div class="logo">
        BugBounty
    </div>

    <div class="nav-links">
        <a href="index.jsp">Home</a>
        <a href="login.jsp">Login</a>
    </div>

</nav>

<div class="form-container">

    <h1>Create Account</h1>

    <p>Register as an ethical hacker</p>

    <form action="register" method="post">

        <label>Full Name</label>
        <input type="text" name="name" required>

        <label>Email</label>
        <input type="email" name="email" required>

        <label>Password</label>
        <input type="password" name="password" required>

        <button type="submit" class="form-button">
            Create Account
        </button>

    </form>

    <p>
        Already have an account?
        <a href="login.jsp">Login</a>
    </p>

</div>

</body>

</html>