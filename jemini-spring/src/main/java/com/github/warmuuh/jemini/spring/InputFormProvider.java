package com.github.warmuuh.jemini.spring;


import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputFormProvider {

	@Setter
	String cssFile;

	public InputStream getInputForm(String inputQuery, String requestURI) throws IOException {
		String cssFileInclude = "";
		if (StringUtils.isNotBlank(cssFile)){
			cssFileInclude = "<link rel=\"stylesheet\" href=\""+cssFile+"\">\n";
		}
		return new ByteArrayInputStream(("<html><head>"
				+ cssFileInclude
				+ "</head><body>"
				+ "<form name=\"input_form\" method=\"get\" action=\""+ requestURI +"\">"
				+ "<label for=\"input\">" + inputQuery + "</label><br>"
				+ "<input type=\"text\" id=\"input\" name=\"\"><br>"
				+ "<input type=\"submit\">"
				+ "</form>"
				+ "<script type=\"text/javascript\">\n"
				+ "window.onload = function() {\n"
				+ "    document.input_form.onsubmit = function(e) {\n"
				+ "        e.preventDefault();\n"
				+ "        var inputValue = document.input_form.elements[0].value;\n"
				+ "        var url = \""+requestURI+"?\" + inputValue;\n"
				+ "        window.location = url;\n"
				+ "        return false;\n"
				+ "    }\n"
				+ "}\n"
				+ "</script>"
				+ "</body></html>").getBytes());
	}
}
