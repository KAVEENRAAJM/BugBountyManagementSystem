
package com.bugbounty.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class CsrfTokenUtil {

    private static final String SESSION_ATTRIBUTE =
            "csrfToken";

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private CsrfTokenUtil() {
        // Utility class; do not instantiate.
    }

    /**
     * Returns the existing session CSRF token or creates one.
     * Creates a session if one does not exist.
     */
    public static String getOrCreateToken(
            HttpServletRequest request) {

        HttpSession session = request.getSession(true);

        synchronized (session) {
            Object existing =
                    session.getAttribute(SESSION_ATTRIBUTE);

            if (existing instanceof String
                    && !((String) existing).isEmpty()) {
                return (String) existing;
            }

            byte[] randomBytes = new byte[32];
            SECURE_RANDOM.nextBytes(randomBytes);

            String token = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(randomBytes);

            session.setAttribute(SESSION_ATTRIBUTE, token);

            return token;
        }
    }

    /**
     * Checks the submitted token against the session token
     * using a constant-time comparison.
     */
    public static boolean isValid(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return false;
        }

        Object expectedObject =
                session.getAttribute(SESSION_ATTRIBUTE);

        String submitted =
                request.getParameter("csrfToken");

        if (!(expectedObject instanceof String)
                || submitted == null
                || submitted.isEmpty()) {
            return false;
        }

        byte[] expectedBytes =
                ((String) expectedObject)
                        .getBytes(java.nio.charset.StandardCharsets.UTF_8);

        byte[] submittedBytes =
                submitted.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return MessageDigest.isEqual(
                expectedBytes,
                submittedBytes
        );
    }
}
