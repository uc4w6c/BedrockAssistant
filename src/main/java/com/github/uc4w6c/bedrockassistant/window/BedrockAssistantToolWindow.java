package com.github.uc4w6c.bedrockassistant.window;

import com.github.uc4w6c.bedrockassistant.window.component.MessagePanel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class BedrockAssistantToolWindow {
  private JPanel mainPanel;
  private JPanel conversationArea;
  private JScrollPane conversationScrollPane;
  private JTextField inputField;
  private JButton sendButton;
  private Function<String, String> sendActionListener;

  private final int MESSAGE_PANEL_MIN_HEIGHT = 50;
  private final int CONVERSATION_AREA_MAX_WIDTH = 250;

  public BedrockAssistantToolWindow(Function<String, String> sendActionListener) {
    this.sendActionListener = sendActionListener;
  }

  public JPanel getContent() {
    mainPanel = new JPanel(new BorderLayout());

    conversationArea = new JPanel();
    conversationArea.setLayout(new BoxLayout(conversationArea, BoxLayout.Y_AXIS));
    conversationArea.setMaximumSize(new Dimension(CONVERSATION_AREA_MAX_WIDTH, Integer.MAX_VALUE));
    conversationScrollPane = new JScrollPane(conversationArea);

    inputField = new JTextField();
    sendButton = new JButton("Send");
    sendButton.addActionListener(actionEvent -> {
      if (StringUtils.isBlank(inputField.getText())) {
        return;
      }
      this.addUserMessage(inputField.getText());
      String result = sendActionListener.apply(inputField.getText());
      this.addSystemMessage(result);
      inputField.setText("");
    });

    JPanel inputPanel = new JPanel(new BorderLayout());
    inputPanel.add(inputField, BorderLayout.CENTER);
    inputPanel.add(sendButton, BorderLayout.EAST);

    mainPanel.add(conversationScrollPane, BorderLayout.CENTER);
    mainPanel.add(inputPanel, BorderLayout.SOUTH);

    return mainPanel;
  }

  public void addUserMessage(String message) {
    this.addConvesertion(AllIcons.General.User, message);
  }

  public void addSystemMessage(String message) {
    this.addConvesertion(AllIcons.Javaee.WebService, message);
  }

  private void addConvesertion(Icon roleIcon, String message) {
    JPanel messagePanel = new MessagePanel(MESSAGE_PANEL_MIN_HEIGHT);
    messagePanel.setLayout(new BorderLayout());
    messagePanel.setBorder(new EmptyBorder(5, 5, 5, 5));

    JLabel iconLabel = new JLabel(roleIcon);
    iconLabel.setBorder(JBUI.Borders.emptyRight(5));

    JTextArea messageArea = new JTextArea(message);
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);
    messageArea.setEditable(false);

    messagePanel.add(iconLabel, BorderLayout.WEST);
    messagePanel.add(messageArea, BorderLayout.CENTER);

    conversationArea.add(messagePanel);
    conversationArea.revalidate();
    conversationArea.repaint();

    messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, messagePanel.getPreferredSize().height));

    // Scroll to the bottom
    ApplicationManager.getApplication().invokeLater(() -> {
      JScrollBar verticalScrollBar = conversationScrollPane.getVerticalScrollBar();
      verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    });
  }

}
