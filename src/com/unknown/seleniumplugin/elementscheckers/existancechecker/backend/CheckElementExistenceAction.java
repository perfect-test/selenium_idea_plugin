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
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ExistenceCheckUtils;
import com.unknown.seleniumplugin.elementscheckers.existancechecker.ui.CheckElementExistenceDialog;
import com.unknown.seleniumplugin.generatefield.backend.FieldsGenerationTemplates;
import com.unknown.seleniumplugin.pluginproperties.GlobalPluginProperties;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import com.unknown.seleniumplugin.utils.PsiCommonUtils;

import java.util.Collection;

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
                locator = getLocatorValue(locatorElement);
                selectorMethodValue = getSelectorValue(locatorElement);
                System.out.println(locator + ":" + selectorMethodValue);
            }
        }
        if(selectorMethodValue != null && locatorElement != null && locator != null) {
            ExistenceCheckUtils.showCheckExistenceDialog(project, locator, selectorMethodValue.getSelectorMethod(),
                    PsiTreeUtil.getParentOfType(locatorElement, PsiClass.class), locatorElement);
        } else {
            showError(project);
        }
    }

    private static void showError(Project project) {
        Messages.showMessageDialog(
                project,
                ERROR_MESSAGE,
                CommonBundle.getErrorTitle(),
                Messages.getErrorIcon()
        );
    }

    private SelectorMethodValue getSelectorValue(PsiElement parent) {
        PsiElement psiElement = parent.getParent();
        if (psiElement != null) {
            PsiElement grandParent = psiElement.getParent();
            if (grandParent != null) {
                if (grandParent instanceof PsiMethodCallExpression) {
                    String text = grandParent.getText();
                    for (SelectorMethodValue selectorMethodValue : SelectorMethodValue.values()) {
                        if (text.contains(selectorMethodValue.getSelectorMethod())) {
                            return selectorMethodValue;
                        }
                    }
                } else {
                    PsiElement masterGrandParent = grandParent.getParent();
                    if (masterGrandParent != null) {
                        if (masterGrandParent instanceof PsiAnnotation) {
                            PsiAnnotation annotation = (PsiAnnotation) masterGrandParent;
                            PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
                            if (referenceElement != null) {
                                if (AnnotationChecker.isFindByAnnotation(referenceElement.getQualifiedName()) ||
                                        AnnotationChecker.isFindBysAnnotation(referenceElement.getQualifiedName())) {
                                    PsiNameValuePair[] nameValuePairs = annotation.getParameterList().getAttributes();
                                    if (nameValuePairs.length > 0) {
                                        for (PsiNameValuePair nameValuePair : nameValuePairs) {
                                            String name = nameValuePair.getName();
                                            if (name != null) {
                                                SelectorMethodValue selectorMethodValue = SelectorMethodValue.getByText(name);
                                                if (selectorMethodValue != null) {
                                                    return selectorMethodValue;
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return null;
    }

    private String getLocatorValue(PsiElement element) {
        return element.getText().replaceAll("\"", "");
    }

}
