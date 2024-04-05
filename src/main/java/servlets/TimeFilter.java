package servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Map;

@WebFilter(value = "/time/*")
public class TimeFilter extends HttpFilter {
    private TemplateEngine engine;
    @Override
    public void init() throws ServletException {
        engine = TemplateImpl.initialize(getServletContext());
    }
    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");
        if(timezone != null && !timezone.isEmpty()&&!isValidZone(timezone)){
            Context context = new Context(req.getLocale(), Map.of("timezone", timezone));
            res.setStatus(400);
            engine.process("invalidpage", context , res.getWriter());
            res.getWriter().close();
        }else
            chain.doFilter(req, res);
    }
    private boolean isValidZone(String timezone) {
        try {
            int offsetHours = Integer.parseInt(timezone.trim().substring(4));
            return offsetHours >= -18 && offsetHours <= 18;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return false;
        }
    }
}
