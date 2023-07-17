package org.gs;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")

public class TimeServlet extends HttpServlet {
    private static final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT);
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.of(getTimezoneParameter(req));
        String formattedInstant = instant.atZone(zoneId).format(formatter);

        resp.setContentType("text/html; charset=utf-8");
        resp.getWriter().print(formattedInstant);
        resp.getWriter().close();
    }

    private String getTimezoneParameter(HttpServletRequest request) {
        String timezone = request.getParameter("timezone");
        if(timezone == null || timezone.isEmpty()) {
            return "UTC";
        }
        return timezone;
    }
}