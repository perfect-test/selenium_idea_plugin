package com.unknown.seleniumplugin.generatefield.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.unknown.seleniumplugin.generatefield.GenerateDialog;
import com.unknown.seleniumplugin.generatefield.backend.FieldsGenerator;
import com.unknown.seleniumplugin.generatefield.ui.GenerateWebElementDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike-sid on 11.04.14.
 */
public class GenerateWebElementAction extends AnAction {


    public void actionPerformed(AnActionEvent e) {
        GenerateWebElementDialog generateWebElementDialog = new GenerateWebElementDialog(e.getProject());
        generateWebElementDialog.show();
        if(generateWebElementDialog.isOK()) {
            PsiClass psiClass = getPsiClassFromContext(e);
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



    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        Editor editor = e.getData(LangDataKeys.EDITOR);
        if(psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

}
