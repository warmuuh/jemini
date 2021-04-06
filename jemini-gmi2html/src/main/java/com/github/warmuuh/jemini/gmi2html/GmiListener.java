package com.github.warmuuh.jemini.gmi2html;

import com.github.warmuuh.jemini.GmiBaseListener;
import com.github.warmuuh.jemini.GmiParser;

import com.github.warmuuh.jemini.GmiParser.ListBlockContext;
import com.github.warmuuh.jemini.GmiParser.ListItemContext;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class GmiListener extends GmiBaseListener {

    private final String cssFile;
    StringBuilder buffer = new StringBuilder();

    public GmiListener(String cssFile) {
        this.cssFile = cssFile;
    }

    @Override
    public void enterGmiFile(GmiParser.GmiFileContext ctx) {
        buffer.append("<html>\n"
            + "<head>\n");

        if (cssFile != null){
            buffer.append("<link rel=\"stylesheet\" href=\""+cssFile+"\">\n");
        }
        buffer.append("</head>\n"
            + "<body>\n");
    }

    @Override
    public void exitGmiFile(GmiParser.GmiFileContext ctx) {
        buffer.append("</body>\n"
            + "</html>\n");
    }

    @Override
    public void enterH1(GmiParser.H1Context ctx) {
        buffer.append("<h1>").append(escapeHtml4(ctx.lineContent().getText())).append("</h1>\n");
    }

    @Override
    public void enterH2(GmiParser.H2Context ctx) {
        buffer.append("<h2>").append(escapeHtml4(ctx.lineContent().getText())).append("</h2>\n");
    }

    @Override
    public void enterH3(GmiParser.H3Context ctx) {
        buffer.append("<h3>").append(escapeHtml4(ctx.lineContent().getText())).append("</h3>\n");
    }

    @Override
    public void enterLink(GmiParser.LinkContext ctx) {
        buffer.append("<a href=\"")
                .append(ctx.url().getText())
                .append("\">")
                .append(escapeHtml4(ctx.lineContent() != null ? ctx.lineContent().getText() : ctx.url().getText()))
                .append("</a><br />\n");
    }

    @Override
    public void enterPreFormatBlock(GmiParser.PreFormatBlockContext ctx) {
        buffer.append("<pre>");
        var text = ctx.getText();
        buffer.append(escapeHtml4(text.replaceAll("```\r?\n", "")));
        buffer.append("</pre>\n");
    }

    @Override
    public void enterListBlock(ListBlockContext ctx) {
        buffer.append("<ul>\n");
        for (ListItemContext listItem : ctx.listItem()) {
            buffer
                .append("<li>")
                .append(escapeHtml4(listItem.lineContent().getText()))
                .append("</li>\n");
        }
        buffer.append("</ul>\n");
    }

    @Override
    public void enterPlainBlock(GmiParser.PlainBlockContext ctx) {
        buffer.append("<p>");
        buffer.append(escapeHtml4(ctx.getText()).replace("\n", "<br />"));
        buffer.append("</p>\n");
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
