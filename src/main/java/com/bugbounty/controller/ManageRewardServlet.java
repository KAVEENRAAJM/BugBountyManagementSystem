
package com.bugbounty.controller;

import java.io.IOException;
import java.util.Locale;

import com.bugbounty.dao.ActivityLogDAO;
import com.bugbounty.dao.RewardDAO;
import com.bugbounty.util.CsrfTokenUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/manageReward")
public class ManageRewardServlet extends HttpServlet {

    private RewardDAO rewardDAO;
    private ActivityLogDAO activityLogDAO;

    @Override
    public void init() throws ServletException {
        rewardDAO = new RewardDAO();
        activityLogDAO = new ActivityLogDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Check login session.
        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        // Only admins can manage rewards.
        String role = String.valueOf(session.getAttribute("role"));

        if (!"ADMIN".equalsIgnoreCase(role)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required."
            );
            return;
        }

        // Validate CSRF token before processing the form.
        if (!CsrfTokenUtil.isValid(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or missing CSRF token. Reload the form and try again."
            );
            return;
        }

        int adminId;

        try {
            adminId = Integer.parseInt(
                    String.valueOf(session.getAttribute("userId"))
            );
        } catch (NumberFormatException e) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get form parameters.
        String bugIdParameter = request.getParameter("bugId");
        String amountParameter = request.getParameter("amount");
        String rewardStatus = request.getParameter("rewardStatus");

        // Validate required parameters.
        if (bugIdParameter == null ||
                bugIdParameter.trim().isEmpty() ||
                amountParameter == null ||
                amountParameter.trim().isEmpty() ||
                rewardStatus == null ||
                rewardStatus.trim().isEmpty()) {

            response.sendRedirect("admin?error=reward");
            return;
        }

        try {
            int bugId = Integer.parseInt(bugIdParameter);
            double amount = Double.parseDouble(amountParameter);

            if (bugId <= 0 ||
                    !Double.isFinite(amount) ||
                    amount < 0) {

                response.sendRedirect("admin?error=reward");
                return;
            }

            // Validate reward status.
            if (!"PENDING".equalsIgnoreCase(rewardStatus) &&
                    !"APPROVED".equalsIgnoreCase(rewardStatus) &&
                    !"PAID".equalsIgnoreCase(rewardStatus)) {

                response.sendRedirect("admin?error=reward");
                return;
            }

            rewardStatus = rewardStatus.toUpperCase(Locale.ROOT);

            // Determine whether this is a new reward or an update.
            boolean rewardAlreadyExists = rewardDAO.rewardExists(bugId);

            boolean success;

            if (rewardAlreadyExists) {
                success = rewardDAO.updateReward(
                        bugId,
                        amount,
                        rewardStatus
                );
            } else {
                success = rewardDAO.createReward(
                        bugId,
                        amount,
                        rewardStatus
                );
            }

            if (success) {

                // Record activity after the reward operation succeeds.
                String action = rewardAlreadyExists
                        ? "REWARD_UPDATED"
                        : "REWARD_CREATED";

                String description =
                        "Admin " + adminId +
                        (rewardAlreadyExists
                                ? " updated the reward."
                                : " created a reward.") +
                        " Amount: " + String.format(
                                Locale.ROOT,
                                "%.2f",
                                amount
                        ) +
                        ", Status: " + rewardStatus + ".";

                try {
                    boolean logged = activityLogDAO.logActivity(
                            adminId,
                            action,
                            description,
                            bugId
                    );

                    if (!logged) {
                        getServletContext().log(
                                "Reward operation succeeded, but activity log "
                                        + "was not saved. Bug ID: " + bugId
                        );
                    }

                } catch (Exception e) {
                    getServletContext().log(
                            "Reward operation succeeded, but activity logging failed.",
                            e
                    );
                }

                response.sendRedirect(
                        "bugDetails?bugId=" + bugId + "&reward=success"
                );

            } else {
                response.sendRedirect("admin?error=reward");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("admin?error=reward");

        } catch (Exception e) {
            getServletContext().log(
                    "Unable to create or update reward.",
                    e
            );

            response.sendRedirect("admin?error=reward");
        }
    }
}
