
package com.bugbounty.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Adds defensive HTTP security headers to application responses.
 *
 * This filter does not replace authentication, authorization,
 * input validation, or CSRF protection.
 */
@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse =
                    (HttpServletResponse) response;

            // Prevent browsers from guessing the response content type.
            httpResponse.setHeader(
                    "X-Content-Type-Options",
                    "nosniff"
            );

            // Prevent the application from being embedded in frames.
            httpResponse.setHeader(
                    "X-Frame-Options",
                    "DENY"
            );

            // Limit referrer information sent to other sites.
            httpResponse.setHeader(
                    "Referrer-Policy",
                    "strict-origin-when-cross-origin"
            );

            // Disable browser features that this application does not need.
            httpResponse.setHeader(
                    "Permissions-Policy",
                    "camera=(), microphone=(), geolocation=()"
            );
        }

        // Continue normal application processing.
        chain.doFilter(request, response);
    }
}
