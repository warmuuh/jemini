package com.github.warmuuh.jemini.gmi2html;

import com.github.warmuuh.jemini.GmiLexer;
import com.github.warmuuh.jemini.GmiParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IOException {
//        String input = "# test1\r\n"
//            + "test1 asdasd asd\r\n"
//            + "test2\r\n\r\n"
//            + "=> http://google.de a google link\r\n";

        GmiLexer lexer = new GmiLexer(CharStreams.fromStream(ParserTest.class.getResourceAsStream("/testinput.txt")));
        GmiParser parser = new GmiParser(new CommonTokenStream(lexer));
        GmiParser.GmiFileContext gmi = parser.gmiFile();

        ParseTreeWalker walker = new ParseTreeWalker();
        GmiListener listener = new GmiListener(null);
        walker.walk(listener, gmi);
        System.out.println(listener.toString());
    }
}
