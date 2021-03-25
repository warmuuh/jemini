package com.github.warmuuh;

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

    throw new ResponseStatusException(geminiInput.sensitive() ? 11 : 10, geminiInput.value(), null);

//    return null;
  }
}
