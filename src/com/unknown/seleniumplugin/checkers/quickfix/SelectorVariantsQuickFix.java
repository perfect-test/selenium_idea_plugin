package com.unknown.seleniumplugin.checkers.quickfix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.unknown.seleniumplugin.checkers.quickfix.ui.FixVariantsDialog;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 15.08.14.
 */
public class SelectorVariantsQuickFix extends BaseIntentionAction {
    private ISelectorChecker selectorChecker;
    private String locator;
    private PsiElement locatorElement;

    public SelectorVariantsQuickFix(ISelectorChecker selectorChecker, String locator, PsiElement locatorElement) {
        this.selectorChecker = selectorChecker;
        this.locator = locator;
        this.locatorElement = locatorElement;
    }

    @NotNull
    @Override
    public String getText() {
        return "Open fix popup";
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
        FixVariantsDialog fixVariantsDialog = new FixVariantsDialog(project, selectorChecker, locator);
        fixVariantsDialog.show();
        if (fixVariantsDialog.isOK()) {
            updateSelectorValue(fixVariantsDialog.geNewSelectorValue(), PsiCommonUtils.getPsiClass(editor, file),
                    getStartSelectorIndex(), getEndSelectorIndex());
        }
    }


    private int getEndSelectorIndex() {
        return locatorElement.getTextRange().getEndOffset() - 1;
    }

    private int getStartSelectorIndex() {
        return locatorElement.getTextRange().getStartOffset() + 1;
    }

    private static void updateSelectorValue(final String newSelectorValue, final PsiClass psiClass,
                                            final int startSelectorIndex, final int endSelectorIndex) {
        System.out.println("NEW selector value : " + newSelectorValue);
        if (startSelectorIndex > 0 && endSelectorIndex > 0 && endSelectorIndex >= startSelectorIndex) {
            new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
                @Override
                protected void run() throws Throwable {
                    Editor editor = FileEditorManager.getInstance(psiClass.getProject()).getSelectedTextEditor();
                    if (editor == null) {
                        return;
                    }
                    Document document = editor.getDocument();
                    CaretModel caretModel = editor.getCaretModel();
                    if(startSelectorIndex == endSelectorIndex) {
                        document.insertString(startSelectorIndex, newSelectorValue);
                    } else{
                        document.replaceString(startSelectorIndex, endSelectorIndex, newSelectorValue);
                    }
                    caretModel.moveToOffset(startSelectorIndex + newSelectorValue.length());
                }

            }.execute();
        }
    }


}
