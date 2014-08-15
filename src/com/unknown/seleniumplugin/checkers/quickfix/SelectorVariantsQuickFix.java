package com.unknown.seleniumplugin.checkers.quickfix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.unknown.seleniumplugin.checkers.quickfix.ui.FixVariantsDialog;
import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ExistenceCheckUtils;
import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 15.08.14.
 */
public class SelectorVariantsQuickFix extends BaseIntentionAction {
    private ISelectorChecker selectorChecker;

    public SelectorVariantsQuickFix(ISelectorChecker selectorChecker) {
        this.selectorChecker = selectorChecker;
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
        CaretModel caretModel = editor.getCaretModel();
        String currentStringText = editor.getDocument().getText(new TextRange(caretModel.getVisualLineStart(),
                caretModel.getVisualLineEnd()));
        if (!currentStringText.isEmpty()) {
            int startOfAnnotation = caretModel.getVisualLineStart() + currentStringText.indexOf(GlobalPluginProperties.SELENIUM_ELEMENT_ANNOTATION);
            currentStringText = currentStringText.trim();
            String locator = getLocator(currentStringText);
            FixVariantsDialog fixVariantsDialog = new FixVariantsDialog(project, selectorChecker, locator);
            fixVariantsDialog.show();
            if (fixVariantsDialog.isOK()) {
                updateSelectorValue(fixVariantsDialog.geNewSelectorValue(), PsiCommonUtils.getPsiClass(editor, file),
                        getStartSelectorIndex(currentStringText), getEndSelectorIndex(currentStringText), startOfAnnotation);
            }

        }
        //TODO:show dialog
    }

    private static String getLocator(String currentStringText) {
        String startEndLocatorValueSymbol = "\"";
        int indexOfStartLocatorValue = currentStringText.indexOf(startEndLocatorValueSymbol);
        if (indexOfStartLocatorValue > 0) {
            currentStringText = currentStringText.substring(indexOfStartLocatorValue + startEndLocatorValueSymbol.length(), currentStringText.length());
            int indexOfEndLocatorValue = currentStringText.indexOf(startEndLocatorValueSymbol);
            if (indexOfEndLocatorValue > 0) {
                return currentStringText.substring(0, indexOfEndLocatorValue);
            }
        }
        return null;
    }

    private static int getEndSelectorIndex(String currentStringText) {
        String elementStartAndEndSymbol = "\"";
        int indexOfSelectorValueStart = currentStringText.indexOf(elementStartAndEndSymbol);
        if (indexOfSelectorValueStart > 0) {
            return currentStringText.indexOf(elementStartAndEndSymbol, indexOfSelectorValueStart + elementStartAndEndSymbol.length());
        }
        return 0;
    }

    private static int getStartSelectorIndex(String currentStringText) {
        return currentStringText.indexOf("\"") + 1;
    }

    private static void updateSelectorValue(final String newSelectorValue, final PsiClass psiClass,
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


}
