package com.github.uc4w6c.bedrockassistant.exceptions;

/**
 * Bedrock error
 */
public class BedrockException extends RuntimeException {
  public BedrockException(Exception e) {
    super(e);
  }
}
