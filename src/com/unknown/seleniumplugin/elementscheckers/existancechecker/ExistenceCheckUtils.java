package com.unknown.seleniumplugin.elementscheckers.existancechecker;

import com.intellij.CommonBundle;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ui.CheckElementExistenceDialog;
import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;

/**
 * Created by mike-sid on 15.08.14.
 */
public class ExistenceCheckUtils {


    public static void showCheckExistenceDialog(Project project, String locator, String findMethod, PsiClass psiClass, PsiElement locatorElement) {
        CheckElementExistenceDialog checkElementExistenceDialog = new CheckElementExistenceDialog(project,
                locator, findMethod);
        checkElementExistenceDialog.show();
        if(checkElementExistenceDialog.isOK()) {
            String newSelectorValue = checkElementExistenceDialog.getNewSelectorVersion();
            if (newSelectorValue != null && !newSelectorValue.isEmpty()) {
                updateSelectorValue(newSelectorValue, psiClass,
                        getStartSelectorIndex(locatorElement), getEndSelectorIndex(locatorElement));
            }
        }
    }

    private static int getEndSelectorIndex(PsiElement locatorElement) {
        return locatorElement.getTextRange().getEndOffset() - 1;
    }

    private static int getStartSelectorIndex(PsiElement locatorElement) {
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

    //TODO: make as baloon error.


    private static String getFindMethod(String currentStringText) {
        for (SelectorMethodValue selectorMethodValue : SelectorMethodValue.values()) {
            if (currentStringText.contains(selectorMethodValue.getSelectorMethod())) {
                return selectorMethodValue.getSelectorMethod();
            }
        }
        return null;
    }

    private static String getLocator(String currentStringText) {
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

    private static boolean isMultiElements(String currentStringText) {
        return currentStringText.contains(GlobalPluginProperties.SELENIUM_MULTI_ELEMENTS_ELEMENT_ANNOTATION);
    }


    private static boolean isContainsLocator(String currentStringText) {
        return currentStringText.contains(GlobalPluginProperties.SELENIUM_ELEMENT_ANNOTATION);
    }
}
