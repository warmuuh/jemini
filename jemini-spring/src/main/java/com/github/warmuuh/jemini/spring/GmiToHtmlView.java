package com.github.warmuuh.jemini.spring;

import com.github.warmuuh.jemini.gmi2html.Gmi2Html;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.Map;

public class GmiToHtmlView implements View {

    private final Gmi2Html gmi2Html;
    private final View delegate;

    public GmiToHtmlView(String cssFile, View delegate) {
        this.gmi2Html = new Gmi2Html();
        gmi2Html.setCssFile(cssFile);
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
        var htmlContent = gmi2Html.translateGmiToHtml(res.toString());
        response.getWriter().write(htmlContent);
    }

}
