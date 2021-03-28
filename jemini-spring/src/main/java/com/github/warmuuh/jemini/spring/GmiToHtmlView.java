package com.github.warmuuh.jemini.spring;

import com.github.warmuuh.jemini.GmiLexer;
import com.github.warmuuh.jemini.GmiParser;
import com.github.warmuuh.jemini.gmi2html.GmiListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
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

        String renderedResult = res.toString();
        if (!renderedResult.endsWith("\n")) {
            renderedResult += "\n";
        }
        GmiLexer lexer = new GmiLexer(CharStreams.fromString(renderedResult));
        GmiParser parser = new GmiParser(new CommonTokenStream(lexer));
        GmiParser.GmiFileContext gmi = parser.gmiFile();

        ParseTreeWalker walker = new ParseTreeWalker();
        GmiListener listener = new GmiListener();
        walker.walk(listener, gmi);


        response.getWriter().write(listener.toString());
    }

}
