package org.gs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            req.setAttribute("timezone", ZoneId.of(getTimezoneParameter(req)));
        } catch (Exception exception) {
            res.setStatus(400);
            res.setContentType("text/html; charset=utf-8");
            res.getWriter().write("<p>Invalid timezone</p>");
            res.getWriter().close();
        }
        chain.doFilter(req, res);
    }

    private String getTimezoneParameter(HttpServletRequest request) {
        String timezone = request.getParameter("timezone");
        if(timezone == null || timezone.isEmpty()) {
            return "UTC";
        }
        return timezone;
    }
}