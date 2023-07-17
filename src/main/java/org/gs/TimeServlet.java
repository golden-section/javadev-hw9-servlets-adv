package org.gs;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "Time Servlet",
            value = "/time")

public class TimeServlet extends HttpServlet {
    private static final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    private TemplateEngine templateEngine;

    @Override
    public void init() {
        JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(webApplication);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT);
        String currentTimezone = getCurrentTimezone(request);

        // Save the latest timezone in a cookie
        Cookie timezoneCookie = new Cookie("lastTimezone", currentTimezone);
        timezoneCookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(timezoneCookie);

        // Get the current time in UTC or the user-selected timezone
        Instant currentTime = Instant.now();
        ZoneId zoneId = ZoneId.of(currentTimezone);
        String formattedTime = currentTime.atZone(zoneId).format(formatter);

        // Get the list of available timezones
        List<String> availableTimezones = ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .toList();

        // Set up Thymeleaf context with the current time
        Context context = new Context(request.getLocale());
        context.setVariable("currentTime", formattedTime);
        context.setVariable("timezones", availableTimezones);
        context.setVariable("currentTimezone", currentTimezone);

        templateEngine.process("time", context, response.getWriter());
    }

    private String getCurrentTimezone(HttpServletRequest req) {
        String timezone = req.getParameter("timezone");
        if(timezone == null || timezone.isEmpty()) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("lastTimezone")) {
                        return cookie.getValue();
                    }
                }
            }
            return "UTC";
        }
        return timezone;
    }
}