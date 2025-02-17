package com.github.uc4w6c.bedrockassistant.action;

import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.github.uc4w6c.bedrockassistant.domain.MessageRole;
import com.github.uc4w6c.bedrockassistant.exceptions.BedrockException;
import com.github.uc4w6c.bedrockassistant.exceptions.TokenException;
import com.github.uc4w6c.bedrockassistant.helper.TokenHelper;
import com.github.uc4w6c.bedrockassistant.popup.MfaPopupComponent;
import com.github.uc4w6c.bedrockassistant.popup.PromptPopupComponent;
import com.github.uc4w6c.bedrockassistant.repository.BedrockRepository;
import com.github.uc4w6c.bedrockassistant.service.BedrockAssistantToolWindowService;
import com.github.uc4w6c.bedrockassistant.state.BedrockAssistantCacheState;
import com.github.uc4w6c.bedrockassistant.state.BedrockAssistantState;
import com.github.uc4w6c.bedrockassistant.state.TokenManager;
import com.github.uc4w6c.bedrockassistant.window.BedrockAssistantToolWindow;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeGenerateAction extends AnAction {
  private final String GENERATE_CODE_PROMPT = """
      The file name is %s.
      
      Here is the surrounding code context:
      ```
      %s
      ```
      
      Write the new code on line %d.
      
      Generate the code according to the instructions below.
      
      Return only the generated code inside triple backticks. Do not include any explanations, comments, or additional text.
      
      %s
      """;

  @Override
  public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
    Project project = anActionEvent.getProject();
    if (project == null) {
      return;
    }

    Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
    if (editor == null) {
      return;
    }

    Consumer<String> submitActionListener = (promptText) -> {
      BedrockAssistantToolWindow bedrockAssistantToolWindow = BedrockAssistantToolWindowService.getInstance(project)
          .getBedrockAssistantToolWindow();

      if (bedrockAssistantToolWindow == null) {
        throw new RuntimeException();
      }
      BedrockAssistantState.State state = BedrockAssistantState.getInstance().getState();

      TokenHelper tokenHelper = new TokenHelper();
      Optional<AwsCredentials> optionalAwsCredentials = tokenHelper.getCredentials(project, state.profile);

      if (optionalAwsCredentials.isEmpty()) {
        return;
      }

      VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
      if (file == null) {
        return;
      }

      String fileName = file.getName();

      Document document = editor.getDocument();

      int totalLines = document.getLineCount();
      CaretModel caretModel = editor.getCaretModel();
      int currentLine = document.getLineNumber(caretModel.getOffset());

      int startLine = Math.max(0, currentLine - 100);
      int endLine = Math.min(totalLines - 1, currentLine + 100);

      int selectedLineIndex = currentLine - startLine + 1;

      int startOffset = document.getLineStartOffset(startLine);
      int endOffset = document.getLineEndOffset(endLine);

      String code = document.getText().substring(startOffset, endOffset);

      String context = String.format(GENERATE_CODE_PROMPT
          ,fileName
          ,code
          ,selectedLineIndex
          ,promptText);
      List<Message> messages = List.of(new Message(MessageRole.USER, context));

      try {
        BedrockRepository bedrockRepository = new BedrockRepository();
        String response = bedrockRepository.get(optionalAwsCredentials.get(), state.region, messages);

        Function<String, String> extractCodeBlock = (responseText) -> {
          Pattern pattern = Pattern.compile("```(.*?)```", Pattern.DOTALL);
          Matcher matcher = pattern.matcher(responseText);

          if (matcher.find()) {
            return matcher.group(1).trim();
          }
          return responseText;
        };

        int caretOffset = caretModel.getOffset();
        int lineNumber = document.getLineNumber(caretOffset);

        if (lineNumber < 0) {
          return;
        }

        int lineEndOffset = document.getLineEndOffset(lineNumber);

        WriteCommandAction.runWriteCommandAction(project, () ->
            document.insertString(lineEndOffset, extractCodeBlock.apply(response)));

        PsiDocumentManager.getInstance(project).commitDocument(document);
      } catch (BedrockException e) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("BedrockAssistantBalloon")
            .createNotification(String.format("""
              Bedrock call failed.
              %s
              """, e.getMessage()),
                NotificationType.ERROR)
            .notify(project);
      }
    };

    ApplicationManager.getApplication().invokeLater(() -> {
      new PromptPopupComponent.PromptPopupComponentBuilder()
          .submitActionListener(submitActionListener)
          .build();
    });
  }
}
