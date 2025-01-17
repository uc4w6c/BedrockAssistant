package com.github.uc4w6c.bedrockassistant.window.component;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel {
  private final int minHeight;

  public MessagePanel(int minHeight) {
    this.minHeight = minHeight;
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    return new Dimension(size.width, Math.max(minHeight, size.height));
  }

  @Override
  public Dimension getMinimumSize() {
    Dimension size = super.getMinimumSize();
    return new Dimension(size.width, minHeight);
  }
}
