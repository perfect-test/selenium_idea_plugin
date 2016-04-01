package com.unknown.seleniumplugin.generatefield.backend;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.*;

/**
 * Created by mike-sid on 04.08.14.
 */
public class FieldsGenerator {

    public static void insertTextAtCurrentCaretPosition(PsiClass psiClass, String generatedFieldText) {
        Editor editor = FileEditorManager.getInstance(psiClass.getProject()).getSelectedTextEditor();
        if (editor == null) {
            return;
        }
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int startLine = caretModel.getVisualLineStart();
        int indexOfCaretPosition = generatedFieldText.indexOf(FieldsGenerationTemplates.CARET_POSITION_PARAM_NAME);
        document.insertString(caretModel.getVisualLineStart(),
                generatedFieldText.replace(FieldsGenerationTemplates.CARET_POSITION_PARAM_NAME, ""));
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        caretModel.moveToOffset(startLine + indexOfCaretPosition);
    }

}
