package com.github.uc4w6c.bedrockassistant.popup;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public class MfaPopupComponent extends JPanel {
  private JBTextField mfaTextfield = new JBTextField();
  private JBPopup popup;

  MfaPopupComponent(String mfaSerial, Consumer<String> okActionListener) {
    super(new BorderLayout());
    this.setBorder(new EmptyBorder(10, 10, 10, 10));
    this.setEnabled(true);

    JBLabel mfaLabel = new JBLabel(String.format("Enter MFA code for device: %s", mfaSerial));
    mfaLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
    this.add(mfaLabel, BorderLayout.NORTH);

    mfaTextfield.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
    mfaTextfield.requestFocus();
    this.add(mfaTextfield, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("cancel");
    cancelButton.addActionListener(actionEvent -> popup.cancel());
    buttonPanel.add(cancelButton);

    JButton okButton = new JButton("OK");
    okButton.addActionListener(actionEvent -> {
      okActionListener.accept(mfaTextfield.getText());
      popup.cancel();
    });
    buttonPanel.add(okButton);

    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  public JComponent getFocusableComponent() {
    return mfaTextfield;
  }

  public void setPopup(JBPopup popup) {
    this.popup = popup;
  }

  public static class MfaPopupComponentBuilder {
    private String mfaSerial;
    private Consumer<String> okActionListener;

    public MfaPopupComponentBuilder mfaSerial(String mfaSerial) {
      this.mfaSerial = mfaSerial;
      return this;
    }

    public MfaPopupComponentBuilder okActionListener(Consumer<String> okActionListener) {
      this.okActionListener = okActionListener;
      return this;
    }

    public MfaPopupComponent build() {
      if (Objects.isNull(this.mfaSerial)
          || Objects.isNull(this.okActionListener)) {
        throw new IllegalArgumentException();
      }

      MfaPopupComponent mfaPopupComponent = new MfaPopupComponent(this.mfaSerial, this.okActionListener);

      JBPopup popup = JBPopupFactory.getInstance()
          .createComponentPopupBuilder(mfaPopupComponent, mfaPopupComponent.getFocusableComponent())
          .setTitle("Enter your mfa to get the AWS token")
          .setResizable(true)
          .setMovable(true)
          .setFocusable(true)
          .setRequestFocus(true)
          .createPopup();
      mfaPopupComponent.setPopup(popup);

      popup.showInFocusCenter();

      return mfaPopupComponent;
    }
  }
}
