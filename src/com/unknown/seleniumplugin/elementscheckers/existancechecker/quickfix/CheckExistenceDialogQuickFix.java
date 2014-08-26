package com.unknown.seleniumplugin.elementscheckers.existancechecker.quickfix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ExistenceCheckUtils;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 15.08.14.
 */
public class CheckExistenceDialogQuickFix extends BaseIntentionAction {

    private ISelectorChecker selectorChecker;
    private String locator;
    private PsiElement locatorElement;

    public CheckExistenceDialogQuickFix(ISelectorChecker selectorChecker, String locator, PsiElement locatorElement) {
        this.selectorChecker = selectorChecker;
        this.locator = locator;
        this.locatorElement = locatorElement;
    }

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
        ExistenceCheckUtils.showCheckExistenceDialog(project, locator, selectorChecker.getName(),
                PsiCommonUtils.getPsiClass(editor, file), locatorElement);
    }
}
