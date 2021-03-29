package com.github.warmuuh.jemini.gmi2html;

import com.github.warmuuh.jemini.GmiBaseListener;
import com.github.warmuuh.jemini.GmiParser;

import com.github.warmuuh.jemini.GmiParser.ListBlockContext;
import com.github.warmuuh.jemini.GmiParser.ListItemContext;
import java.util.List;

public class GmiListener extends GmiBaseListener {

    StringBuilder buffer = new StringBuilder();

    @Override
    public void enterGmiFile(GmiParser.GmiFileContext ctx) {
        buffer.append("<html>\n"
            + "<head>\n"
            + "</head>\n"
            + "<body>\n");
    }

    @Override
    public void exitGmiFile(GmiParser.GmiFileContext ctx) {
        buffer.append("</body>\n"
            + "</html>\n");
    }

    @Override
    public void enterH1(GmiParser.H1Context ctx) {
        buffer.append("<h1>").append(ctx.lineContent().getText()).append("</h1>\n");
    }

    @Override
    public void enterH2(GmiParser.H2Context ctx) {
        buffer.append("<h2>").append(ctx.lineContent().getText()).append("</h2>\n");
    }

    @Override
    public void enterH3(GmiParser.H3Context ctx) {
        buffer.append("<h3>").append(ctx.lineContent().getText()).append("</h3>\n");
    }

    @Override
    public void enterLink(GmiParser.LinkContext ctx) {
        buffer.append("<a href=\"")
                .append(ctx.url().getText())
                .append("\">")
                .append(ctx.lineContent() != null ? ctx.lineContent().getText() : ctx.url().getText())
                .append("</a><br />\n");
    }

    @Override
    public void enterPreFormatBlock(GmiParser.PreFormatBlockContext ctx) {
        buffer.append("<pre>");
        List<GmiParser.PreFormatContext> preLine = ctx.preFormat();
        for (int i = 0; i < preLine.size(); i++) {
            GmiParser.PreFormatContext line = preLine.get(i);
            if (i > 0){
                buffer.append("\n");
            }
            buffer.append(line.lineContent().getText());
        }
        buffer.append("</pre>\n");
    }

    @Override
    public void enterListBlock(ListBlockContext ctx) {
        buffer.append("<ul>\n");
        for (ListItemContext listItem : ctx.listItem()) {
            buffer
                .append("<li>")
                .append(listItem.lineContent().getText())
                .append("</li>\n");
        }
        buffer.append("</ul>\n");
    }

    @Override
    public void enterPlainBlock(GmiParser.PlainBlockContext ctx) {
        buffer.append("<p>");
        List<GmiParser.LineContentContext> lineContent = ctx.lineContent();
        for (int i = 0; i < lineContent.size(); i++) {
            GmiParser.LineContentContext line = lineContent.get(i);
            if (i > 0){
                buffer.append("<br />\n");
            }
            buffer.append(line.getText());

        }
        buffer.append("</p>\n");
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
