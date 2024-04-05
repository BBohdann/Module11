package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = TemplateImpl.initialize(getServletContext());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String timezone = req.getParameter("timezone");
        if (hasCookie(req, "lastTimezone")) {
            timezone = getCookieValue(req, "lastTimezone");
        }

        ZonedDateTime currentTime = null;
        if (timezone != null && !timezone.isEmpty()) {
            if (timezone.startsWith("UTC")) {
                int hours = Integer.parseInt(timezone.trim().substring(4));
                currentTime = ZonedDateTime.now(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(hours)));
                addCookie(resp, "lastTimezone", timezone.replace(" ", "+"));
            }
        } else {
            currentTime = ZonedDateTime.now(ZoneId.of("UTC"));
            timezone = "UTC";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        String formattedTime = currentTime.format(formatter);

        Context info = new Context(req.getLocale(), Map.of("time", formattedTime, "timezone", timezone));
        engine.process("time", info, resp.getWriter());
        resp.getWriter().close();
    }

    private boolean hasCookie(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getCookieValue(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void addCookie(HttpServletResponse resp, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        resp.addCookie(cookie);
    }
}