package servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

public class TemplateImpl {
    public static TemplateEngine initialize(ServletContext servletContext) throws ServletException {
        TemplateEngine engine = new TemplateEngine();
        JakartaServletWebApplication jswa =
                JakartaServletWebApplication.buildApplication(servletContext);
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(jswa);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
        return engine;
    }
}
