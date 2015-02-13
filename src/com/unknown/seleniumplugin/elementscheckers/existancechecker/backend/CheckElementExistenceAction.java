package com.unknown.seleniumplugin.elementscheckers.existancechecker.backend;

import com.intellij.CommonBundle;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ExistenceCheckUtils;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 05.08.14.
 */
public class CheckElementExistenceAction extends AnAction {
    private static final String ERROR_MESSAGE = "Check existence can be done only on @FindBy of findElement(s) function. Move cursor to locator value and try again";


    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(LangDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        Document document = editor.getDocument();
        if (!document.isWritable()) {
            return;
        }
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        int offset = editor.getCaretModel().getOffset();
        if (psiFile == null) {
            return;
        }
        String locator = null;
        SelectorMethodValue selectorMethodValue = null;
        PsiElement locatorElement = null;
        PsiElement elementAt = psiFile.findElementAt(offset);
        if (elementAt != null) {
            locatorElement = elementAt.getParent();
            if (locatorElement != null && locatorElement instanceof PsiLiteralExpression) {
                locator = PsiCommonUtils.getLocatorValue(locatorElement);
                selectorMethodValue = PsiCommonUtils.getSelectorMethodValue(locatorElement);
                System.out.println(locator + ":" + selectorMethodValue);
            }
        }
        if (selectorMethodValue != null && locatorElement != null && locator != null) {
            ExistenceCheckUtils.showCheckExistenceDialog(project, locator, selectorMethodValue.getSelectorMethod(),
                    PsiTreeUtil.getParentOfType(locatorElement, PsiClass.class), locatorElement);
        } else {
            showError(project);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Project project = e.getProject();
        if(project != null && psiFile!=null) {
            final Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
            if(editor != null) {
                int offset = editor.getCaretModel().getOffset();
                PsiElement elementAt = psiFile.findElementAt(offset);
                if(elementAt != null) {
                    PsiElement locatorElement = elementAt.getParent();
                    if (locatorElement != null && locatorElement instanceof PsiLiteralExpression) {
                        if(PsiCommonUtils.getLocatorValue(locatorElement)!= null) {
                            e.getPresentation().setEnabled(true);
                            e.getPresentation().setVisible(true);
                            return;
                        }
                    }
                }

            }
        }
        e.getPresentation().setEnabled(false);
        e.getPresentation().setVisible(false);

    }

    private static void showError(Project project) {
        Messages.showMessageDialog(
                project,
                ERROR_MESSAGE,
                CommonBundle.getErrorTitle(),
                Messages.getErrorIcon()
        );
    }


}
