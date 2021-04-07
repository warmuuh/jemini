package com.github.warmuuh.jemini.gmi2html;


import com.github.warmuuh.jemini.GmiLexer;
import com.github.warmuuh.jemini.GmiParser;
import lombok.Setter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class Gmi2Html {

	@Setter
	private String cssFile;

	public String translateGmiToHtml(String gmiContent){
		if (!gmiContent.endsWith("\n")) {
			gmiContent += "\n";
		}
		GmiLexer lexer = new GmiLexer(CharStreams.fromString(gmiContent));
		GmiParser parser = new GmiParser(new CommonTokenStream(lexer));
		GmiParser.GmiFileContext gmi = parser.gmiFile();

		ParseTreeWalker walker = new ParseTreeWalker();
		GmiListener listener = new GmiListener(cssFile);
		walker.walk(listener, gmi);

		return listener.toString();
	}

}
