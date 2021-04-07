package com.github.warmuuh.jemini.spring;

import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class GeminiInputHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  private final InputFormProvider inputFormProvider;

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
      return URLDecoder.decode(queryString, StandardCharsets.UTF_8);
    }

    if (((ServletWebRequest) webRequest).getRequest().getScheme().contains("gemini")){
      throw new ResponseStatusException(geminiInput.sensitive() ? 11 : 10, geminiInput.value(), null);
    } else {
      renderInputForm((ServletWebRequest) webRequest, geminiInput);
      throw new ResponseStatusException(400, geminiInput.value(), null);
    }
  }

  private void renderInputForm(ServletWebRequest webRequest, GeminiInput geminiInput) throws IOException {
    HttpServletResponse response = webRequest.getResponse();
    response.setStatus(200);
    String requestURI = webRequest.getRequest().getRequestURI();
    var formContent = inputFormProvider.getInputForm(geminiInput.value(), requestURI);
    formContent.transferTo(response.getOutputStream());
    response.getOutputStream().close();
  }
}
