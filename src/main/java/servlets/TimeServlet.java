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
        engine = new TemplateEngine();
        JakartaServletWebApplication jswa =
                JakartaServletWebApplication.buildApplication(this.getServletContext());
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(jswa);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String timezone = req.getParameter("timezone");
        if (isCookie(req)) {
            Cookie[] cookies = req.getCookies();
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    String cookieValue = cookie.getValue();
                    if (cookieName.equals("lastTimezone")) {
                        timezone = cookieValue;
                }
            }
        }

        ZonedDateTime currentTime = null;
        if (timezone != null && !timezone.isEmpty()) {
            if (timezone.startsWith("UTC")) {
                int hours = Integer.parseInt(timezone.trim().substring(4));
                currentTime = ZonedDateTime.now(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(hours)));
                Cookie cookie = new Cookie("lastTimezone", timezone.replace(" ", "+"));
                resp.addCookie(cookie);
            }
        } else {
            currentTime = ZonedDateTime.now(ZoneId.of("UTC"));
            timezone = "UTC";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        String formattedTime = currentTime.format(formatter);

        Context info = new Context(req.getLocale(), Map.of("time", formattedTime, "timezone", timezone));
        engine.process("time", info , resp.getWriter());
        resp.getWriter().close();
    }

    public boolean isCookie(HttpServletRequest req){
        boolean flag = true;
        if(req.getCookies() == null){
            flag = false;
        }
        return flag;
    }
}