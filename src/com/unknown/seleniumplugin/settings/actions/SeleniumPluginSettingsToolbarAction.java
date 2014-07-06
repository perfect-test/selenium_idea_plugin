package com.unknown.seleniumplugin.settings.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.unknown.seleniumplugin.settings.ui.SeleniumSettingsDialog;

/**
 * Created by mike-sid on 20.06.14.
 */
public class SeleniumPluginSettingsToolbarAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        SeleniumSettingsDialog seleniumSettingsDialog = new SeleniumSettingsDialog(e.getProject());
        seleniumSettingsDialog.show();
    }
}
