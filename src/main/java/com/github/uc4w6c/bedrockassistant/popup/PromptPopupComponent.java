package com.github.uc4w6c.bedrockassistant.popup;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public class PromptPopupComponent extends JPanel {
  private JBTextField promptTextfield = new JBTextField();
  private JBPopup popup;

  PromptPopupComponent(Consumer<String> submitActionListener) {
    super(new BorderLayout());
    this.setBorder(new EmptyBorder(10, 10, 10, 10));
    this.setEnabled(true);

    JBLabel mfaLabel = new JBLabel("Enter instructions for generating code");
    mfaLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
    this.add(mfaLabel, BorderLayout.NORTH);

    promptTextfield.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
    promptTextfield.requestFocus();
    promptTextfield.setColumns(80);
    this.add(promptTextfield, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton submitButton = new JButton("submit");
    submitButton.addActionListener(actionEvent -> {
      submitActionListener.accept(promptTextfield.getText());
      popup.cancel();
    });
    buttonPanel.add(submitButton);

    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  public JComponent getFocusableComponent() {
    return promptTextfield;
  }

  public void setPopup(JBPopup popup) {
    this.popup = popup;
  }

  public static class PromptPopupComponentBuilder {
    private Consumer<String> submitActionListener;

    public PromptPopupComponentBuilder submitActionListener(Consumer<String> submitActionListener) {
      this.submitActionListener = submitActionListener;
      return this;
    }

    public PromptPopupComponent build() {
      if (Objects.isNull(this.submitActionListener)) {
        throw new IllegalArgumentException();
      }

      PromptPopupComponent promptPopupComponent = new PromptPopupComponent(submitActionListener);

      JBPopup popup = JBPopupFactory.getInstance()
          .createComponentPopupBuilder(promptPopupComponent, promptPopupComponent.getFocusableComponent())
          .setResizable(true)
          .setMovable(true)
          .setFocusable(true)
          .setRequestFocus(true)
          .createPopup();
      promptPopupComponent.setPopup(popup);

      popup.showInFocusCenter();

      return promptPopupComponent;
    }
  }
}
