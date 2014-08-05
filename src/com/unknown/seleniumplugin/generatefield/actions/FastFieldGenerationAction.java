package com.unknown.seleniumplugin.generatefield.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;

/**
 * Created by mike-sid on 05.08.14.
 */
public class FastFieldGenerationAction extends EditorAction {
    private static final String CARET_POSITION_PARAM_NAME = "{cp}";
    private static final String ANNOTATION_TEMPLATE = "\t@FindBy(css=\"" + CARET_POSITION_PARAM_NAME + "\")\n" +
            "\tprivate " + GlobalPluginProperties.WEB_ELEMENT_FIELD_TYPE + " webElement;\n";

    public FastFieldGenerationAction() {
        this(new UpHandler());
    }

    protected FastFieldGenerationAction(EditorActionHandler defaultHandler) {
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
