package com.github.warmuuh.jemini.spring;

import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

public class GeminiInputHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(GeminiInput.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    var geminiInput = parameter.getParameterAnnotation(GeminiInput.class);

    var queryString = ((ServletWebRequest) webRequest).getRequest().getQueryString();
    if (StringUtils.hasLength(queryString)){
      return queryString;
    }

    if (((ServletWebRequest) webRequest).getRequest().getProtocol().contains("gemini")){
      throw new ResponseStatusException(geminiInput.sensitive() ? 11 : 10, geminiInput.value(), null);
    } else {
      HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();
      response.setStatus(200);
      String requestURI = ((ServletWebRequest) webRequest).getRequest().getRequestURI();
      response.getOutputStream().println("<html><head></head><body>"
          + "<form name=\"input_form\" method=\"get\" action=\""+ requestURI +"\">"
          + "<label for=\"input\">" + geminiInput.value() + "</label><br>"
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
          + "</body></html>");
      response.getOutputStream().close();
      throw new ResponseStatusException(400, geminiInput.value(), null);
    }

//    return null;
  }
}
