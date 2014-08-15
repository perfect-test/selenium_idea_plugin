package com.unknown.seleniumplugin.elementscheckers.existancechecker.quickfix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ExistenceCheckUtils;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 15.08.14.
 */
public class CheckExistenceDialogQuickFix extends BaseIntentionAction {

    @NotNull
    @Override
    public String getText() {
        return "Check element existence and fix";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Selenium";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        ExistenceCheckUtils.showCheckExistenceDialog(project, editor, editor.getDocument(), PsiCommonUtils.getPsiClass(editor, file));
    }
}
