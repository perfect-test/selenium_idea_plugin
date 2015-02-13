package com.unknown.seleniumplugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;

/**
 * Created by mike-sid on 15.08.14.
 */
public class PsiCommonUtils {

    public static PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        Editor editor = e.getData(LangDataKeys.EDITOR);
        if(psiFile == null || editor == null) {
            return null;
        }
        return getPsiClass(editor, psiFile);
    }


    public static PsiClass getPsiClass(Editor editor, PsiFile psiFile){
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);

    }


    public static SelectorMethodValue getSelectorMethodValue(PsiElement parent) {
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

    public static String getLocatorValue(PsiElement element) {
        return element.getText().replaceAll("\"", "");
    }

}
