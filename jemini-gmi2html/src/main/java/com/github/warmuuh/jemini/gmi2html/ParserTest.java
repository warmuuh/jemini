package com.github.warmuuh.jemini.gmi2html;

import com.github.warmuuh.jemini.GmiLexer;
import com.github.warmuuh.jemini.GmiParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class ParserTest {
    public static void main(String[] args) {
        String input = """
                # test1
                test1 asdasd asd
                test2
                => http://google.de a google link
                """;
        GmiLexer lexer = new GmiLexer(CharStreams.fromString(input));
        GmiParser parser = new GmiParser(new CommonTokenStream(lexer));
        GmiParser.GmiFileContext gmi = parser.gmiFile();

        ParseTreeWalker walker = new ParseTreeWalker();
        GmiListener listener = new GmiListener();
        walker.walk(listener, gmi);
        System.out.println(listener.toString());
    }
}
