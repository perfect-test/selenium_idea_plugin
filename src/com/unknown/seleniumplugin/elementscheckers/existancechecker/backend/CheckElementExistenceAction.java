package com.unknown.seleniumplugin.elementscheckers.existancechecker.backend;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.find.FindBundle;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ui.CheckElementExistenceDialog;
import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;

import java.util.Collection;

/**
 * Created by mike-sid on 05.08.14.
 */
public class CheckElementExistenceAction extends AnAction {
    private static final String ERROR_MESSAGE = "Check couldn't be done on not @FindBy . Move cursor to annotation and try again";

    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(LangDataKeys.EDITOR);
        if ( editor == null) {
            return;
        }
        Document document = editor.getDocument();
        if (!document.isWritable()) {
            return;
        }
        String locator = null;
        String findMethod = null;
        String currentStringText = document.getText(new TextRange(editor.getCaretModel().getVisualLineStart(),
                editor.getCaretModel().getVisualLineEnd())).trim();
        if(currentStringText.isEmpty() || !isContainsLocator(currentStringText)) {
            showError(e.getProject());
        } else {
            findMethod = getFindMethod(currentStringText);
            if(findMethod != null) {
                locator = getLocator(currentStringText);
            }
            CheckElementExistenceDialog checkElementExistenceDialog = new CheckElementExistenceDialog(e.getProject(), locator, findMethod, isMultiElements(currentStringText));
            checkElementExistenceDialog.show();
        }
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
        for(SelectorMethodValue selectorMethodValue : SelectorMethodValue.values()) {
            if(currentStringText.contains(selectorMethodValue.getSelectorMethod())){
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
