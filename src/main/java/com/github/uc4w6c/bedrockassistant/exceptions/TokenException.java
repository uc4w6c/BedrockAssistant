package com.github.uc4w6c.bedrockassistant.exceptions;

/**
 * AWS token error
 */
public class TokenException extends RuntimeException {
  public TokenException(Exception e) {
    super(e);
  }
}
