package com.unknown.seleniumplugin.generatefield.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by mike-sid on 23.03.15.
 */
public class GenerateByInstanceAction extends EditorAction {
    private static final String CARET_POSITION_PARAM_NAME = "{cp}";
    private static final String TEMPLATE = "\t" + "By by = By.cssSelector(\""+ CARET_POSITION_PARAM_NAME + "\");\n";

    public GenerateByInstanceAction() {
        this(new GenerateByHandler());
    }

    protected GenerateByInstanceAction(EditorActionHandler defaultHandler) {
        super(defaultHandler);
    }
	private By by = By.cssSelector("");


    private static class GenerateByHandler extends EditorWriteActionHandler {
        private GenerateByHandler() {
        }

        @Override
        public void executeWriteAction(Editor editor, DataContext dataContext) {
            if (editor == null) {
                return;
            }
            Document document = editor.getDocument();
            if (!document.isWritable()) {
                return;
            }
            CaretModel caretModel = editor.getCaretModel();
            int lineStartOffset = caretModel.getVisualLineStart();
            int indexOfCaretPosition = TEMPLATE.indexOf(CARET_POSITION_PARAM_NAME);
            document.insertString(lineStartOffset, TEMPLATE.replace(CARET_POSITION_PARAM_NAME, ""));
            caretModel.moveToOffset(lineStartOffset + indexOfCaretPosition);
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
    }
}
