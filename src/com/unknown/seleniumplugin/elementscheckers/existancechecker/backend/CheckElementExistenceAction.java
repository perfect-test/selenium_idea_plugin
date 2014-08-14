package com.unknown.seleniumplugin.elementscheckers.existancechecker.backend;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.find.FindBundle;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ui.CheckElementExistenceDialog;
import com.unknown.seleniumplugin.generatefield.backend.FieldsGenerationTemplates;
import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;

import java.util.Collection;

/**
 * Created by mike-sid on 05.08.14.
 */
public class CheckElementExistenceAction extends AnAction {
    private static final String ERROR_MESSAGE = "Check couldn't be done on not @FindBy . Move cursor to annotation and try again";

    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(LangDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        Document document = editor.getDocument();
        if (!document.isWritable()) {
            return;
        }
        String locator = null;
        String findMethod = null;
        CaretModel caretModel = editor.getCaretModel();
        String currentStringText = document.getText(new TextRange(caretModel.getVisualLineStart(),
                caretModel.getVisualLineEnd()));
        if (currentStringText.isEmpty() || !isContainsLocator(currentStringText)) {
            showError(e.getProject());
        } else {
            int startOfAnnotation = caretModel.getVisualLineStart() + currentStringText.indexOf(GlobalPluginProperties.SELENIUM_ELEMENT_ANNOTATION);
            currentStringText = currentStringText.trim();
            findMethod = getFindMethod(currentStringText);
            if (findMethod != null) {
                locator = getLocator(currentStringText);
            }
            CheckElementExistenceDialog checkElementExistenceDialog = new CheckElementExistenceDialog(e.getProject(),
                    locator, findMethod, isMultiElements(currentStringText));
            checkElementExistenceDialog.show();
            if(checkElementExistenceDialog.isOK()) {
                String newSelectorValue = checkElementExistenceDialog.getNewSelectorVersion();
                if (newSelectorValue != null && !newSelectorValue.isEmpty()) {
                    updateSelectorValue(newSelectorValue, getPsiClassFromContext(e),
                            getStartSelectorIndex(currentStringText), getEndSelectorIndex(currentStringText), startOfAnnotation);
                }
            }
        }
    }

    private int getEndSelectorIndex(String currentStringText) {
        String elementStartAndEndSymbol = "\"";
        int indexOfSelectorValueStart = currentStringText.indexOf(elementStartAndEndSymbol);
        if (indexOfSelectorValueStart > 0) {
            return currentStringText.indexOf(elementStartAndEndSymbol, indexOfSelectorValueStart + elementStartAndEndSymbol.length());
        }
        return 0;
    }

    private int getStartSelectorIndex(String currentStringText) {
        return currentStringText.indexOf("\"") + 1;
    }

    private void updateSelectorValue(final String newSelectorValue, final PsiClass psiClass,
                                     final int startSelectorIndex, final int endSelectorIndex, final int startOfAnnotation) {
        System.out.println("NEW selector value : " + newSelectorValue);
        if (startSelectorIndex > 0 && endSelectorIndex > 0 && startOfAnnotation >= 0 && endSelectorIndex > startSelectorIndex) {
            new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
                @Override
                protected void run() throws Throwable {
                    Editor editor = FileEditorManager.getInstance(psiClass.getProject()).getSelectedTextEditor();
                    if (editor == null) {
                        return;
                    }
                    Document document = editor.getDocument();
                    CaretModel caretModel = editor.getCaretModel();
                    int startOfReplace = startOfAnnotation + startSelectorIndex;
                    int endOfReplace = startOfAnnotation + endSelectorIndex;
                    document.replaceString(startOfReplace, endOfReplace, newSelectorValue);
                    caretModel.moveToOffset(startOfAnnotation + startSelectorIndex + newSelectorValue.length());
                }

            }.execute();
        }
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        Editor editor = e.getData(LangDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }


    //TODO: make as baloon error.
    private void showError(Project project) {
        Messages.showMessageDialog(
                project,
                ERROR_MESSAGE,
                CommonBundle.getErrorTitle(),
                Messages.getErrorIcon()
        );
    }

    private String getFindMethod(String currentStringText) {
        for (SelectorMethodValue selectorMethodValue : SelectorMethodValue.values()) {
            if (currentStringText.contains(selectorMethodValue.getSelectorMethod())) {
                return selectorMethodValue.getSelectorMethod();
            }
        }
        return null;
    }

    private String getLocator(String currentStringText) {
        if (isContainsLocator(currentStringText)) {
            String startEndLocatorValueSymbol = "\"";
            int indexOfStartLocatorValue = currentStringText.indexOf(startEndLocatorValueSymbol);
            if (indexOfStartLocatorValue > 0) {
                currentStringText = currentStringText.substring(indexOfStartLocatorValue + startEndLocatorValueSymbol.length(), currentStringText.length());
                int indexOfEndLocatorValue = currentStringText.indexOf(startEndLocatorValueSymbol);
                if (indexOfEndLocatorValue > 0) {
                    return currentStringText.substring(0, indexOfEndLocatorValue);
                }
            }
        }
        return null;
    }

    private boolean isMultiElements(String currentStringText) {
        return currentStringText.contains(GlobalPluginProperties.SELENIUM_MULTI_ELEMENTS_ELEMENT_ANNOTATION);
    }


    private boolean isContainsLocator(String currentStringText) {
        return currentStringText.contains(GlobalPluginProperties.SELENIUM_ELEMENT_ANNOTATION);
    }
}
