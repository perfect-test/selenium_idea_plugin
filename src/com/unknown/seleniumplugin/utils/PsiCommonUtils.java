package com.unknown.seleniumplugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by mike-sid on 15.08.14.
 */
public class PsiCommonUtils {

    public static PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        Editor editor = e.getData(LangDataKeys.EDITOR);
        if(psiFile == null || editor == null) {
            return null;
        }
        return getPsiClass(editor, psiFile);
    }


    public static PsiClass getPsiClass(Editor editor, PsiFile psiFile){
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);

    }
}
