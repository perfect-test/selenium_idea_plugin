package com.unknown.seleniumplugin.generateannotation;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

/**
 * Created by mike-sid on 08.04.14.
 */
public class CreateSeleniumAnnotation extends EditorAction {
    private static final String CARET_POSITION_PARAM_NAME = "{cp}";
    private static final String ANNOTATION_TEMPLATE = "\t@FindBy(css=\"" + CARET_POSITION_PARAM_NAME + "\")\n";

    public CreateSeleniumAnnotation() {
        this(new UpHandler());
    }

    protected CreateSeleniumAnnotation(EditorActionHandler defaultHandler) {
        super(defaultHandler);
    }


    private static class UpHandler extends EditorWriteActionHandler {
        private UpHandler() {
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
            int indexOfCaretPosition = ANNOTATION_TEMPLATE.indexOf(CARET_POSITION_PARAM_NAME);
            document.insertString(lineStartOffset, ANNOTATION_TEMPLATE.replace(CARET_POSITION_PARAM_NAME, ""));
            caretModel.moveToOffset(lineStartOffset + indexOfCaretPosition);
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
    }



}
