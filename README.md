# Bug Bounty Management System

A web-based application for managing bug bounty reports, security findings, evidence uploads, and rewards.

## Features

- User registration and login
- Role-based access for hackers and administrators
- Bug report submission and tracking
- Admin dashboard for managing reports and statuses
- Evidence upload and download controls
- Reward and bounty management
- Activity logs
- Session management and CSRF protection

## Tech Stack

- **Frontend:** HTML, CSS, JSP
- **Backend:** Java, Servlets
- **Database:** MariaDB
- **Server:** Apache Tomcat 11
- **Build Tool:** Maven
- **Password Security:** BCrypt

## Prerequisites

- Java 21 or a compatible version
- Apache Maven
- MariaDB
- Apache Tomcat 11

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/KAVEENRAAJM/BugBountyManagementSystem.git
   cd BugBountyManagementSystem
   ```

2. Create a MariaDB database named `bug_bounty_db`.

3. Configure the database URL, username, and password using environment variables:

   - `DB_URL`
   - `DB_USER`
   - `DB_PASSWORD`

4. Build the project:

   ```bash
   mvn clean package
   ```

5. Deploy the generated WAR file to Tomcat's `webapps` directory.

6. Start Tomcat and open:

   `http://localhost:8080/BugBountyManagementSystem/`

## Security

This project includes authentication, role-based access controls, CSRF validation, security headers, and evidence-access restrictions.

These controls should be independently tested before the application is used in a production environment.

## Author

**KAVEENRAAJM**

## Disclaimer

Educational cybersecurity project. Use only in authorized environments.
