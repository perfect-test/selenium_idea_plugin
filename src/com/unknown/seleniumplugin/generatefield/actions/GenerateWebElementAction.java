package com.unknown.seleniumplugin.generatefield.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.unknown.seleniumplugin.generatefield.backend.FieldsGenerator;
import com.unknown.seleniumplugin.generatefield.ui.GenerateWebElementDialog;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;

/**
 * Created by mike-sid on 11.04.14.
 */
public class GenerateWebElementAction extends AnAction {


    public void actionPerformed(AnActionEvent e) {
        GenerateWebElementDialog generateWebElementDialog = new GenerateWebElementDialog(e.getProject());
        generateWebElementDialog.show();
        if(generateWebElementDialog.isOK()) {
            PsiClass psiClass = PsiCommonUtils.getPsiClassFromContext(e);
            generateField(psiClass, generateWebElementDialog.getGeneratedFieldText());

        }

    }

    private void generateField(final PsiClass psiClass, final String generatedFieldText) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                FieldsGenerator.insertTextAtCurrentCaretPosition(psiClass, generatedFieldText);
            }

        }.execute();
    }

}
