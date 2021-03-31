package com.github.warmuuh.jemini.spring;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import java.util.Locale;


@Order(Ordered.HIGHEST_PRECEDENCE)
public class GmiToHtmlViewResolver implements ViewResolver {

    private final String cssFile;
    private final AbstractTemplateViewResolver delegate;

    public GmiToHtmlViewResolver(String cssFile, AbstractTemplateViewResolver delegate) {
        this.cssFile = cssFile;
        this.delegate = delegate;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        return new GmiToHtmlView(cssFile, delegate.resolveViewName(viewName, locale));
    }
}
