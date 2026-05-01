package com.microfinanceBank.apigateway.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * API 게이트웨이 공통 상수 유틸리티 클래스.
 * XSS 방지를 위한 정규식 패턴 목록을 정의한다.
 */
public class ConstantUtil {

  /**
   * XSS 필터에서 제거할 위험 패턴 목록.
   * 스크립트 태그, eval(), javascript: URI, src 속성, vbscript: 등을 포함한다.
   */
  public static final Set<Pattern> FILTER_PATTERNS = Set.of(

          // 일반적인 HTML 태그 제거
          Pattern.compile("(<input(.*?)></input>|<input(.*)/>)", Pattern.CASE_INSENSITIVE),
          Pattern.compile("<span(.*?)</span>", Pattern.CASE_INSENSITIVE),
          Pattern.compile("<div(.*)</div>", Pattern.CASE_INSENSITIVE),
          Pattern.compile("<style>(.*?)</style>", Pattern.CASE_INSENSITIVE),
          //Avoid onload= expressions
          Pattern.compile("onload(.*?)=",
                  Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
          // Avoid anything between script tags
          Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
          // Avoid javascript:... expressions
          Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
          // Remove any lonesome </script> tag
          Pattern.compile("</script>", Pattern.CASE_INSENSITIVE), Pattern.compile("<script(.*?)>",
                  Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
          // Avoid anything in a src='...' type of expression
          Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
                  Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
          Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
                  Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
          // Avoid eval(...) expressions
          Pattern.compile("eval\\((.*?)\\)",
                  Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
          Pattern.compile("expression\\((.*?)\\)",
                  Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
          // Avoid vbscript:... expressions
          Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE)

  );

  public static final String EMPTY = "";

}
