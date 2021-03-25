package com.github.warmuuh;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeminiInput {

  /**
   * the input name. will be shown as input query to the suer
   */
  String value();

  /**
   * if the input is sensitive
   */
  boolean sensitive() default false;

}
