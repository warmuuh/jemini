package com.github.warmuuh.jemini.spring;

import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.Map;

public class GmiToHtmlView implements View {

    private final View delegate;

    public GmiToHtmlView(View delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getContentType() {
        return "text/html; charset=utf-8";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        var res = new StringWriter();

        delegate.render(model, request, new CatchBodyResponseWrapper(res));
        // parse gmi and write html
        response.getWriter().write(res.toString());
    }

}
